import { WebSocketServer } from 'ws';
import { randomUUID } from 'node:crypto';

const port = Number(process.env.PORT || 8787);
const wss = new WebSocketServer({ port });
const peers = new Map();

function send(ws, payload) {
  if (ws && ws.readyState === 1) ws.send(JSON.stringify(payload));
}

wss.on('connection', (ws) => {
  let code = null;
  let deviceId = null;
  let peerCode = null;

  ws.on('message', (raw) => {
    try {
      const msg = JSON.parse(raw.toString());

      if (msg.type === 'hello') {
        code = String(msg.code || '').toUpperCase();
        deviceId = String(msg.deviceId || '');
        if (!/^[A-Z0-9]{4}-[A-Z0-9]{4}$/.test(code) || !deviceId) {
          ws.close(1008, 'invalid identity');
          return;
        }
        const old = peers.get(code);
        if (old && old !== ws) old.close(4001, 'replaced');
        peers.set(code, ws);
        send(ws, { type: 'status', value: 'registered' });
        return;
      }

      if (msg.type === 'pair') {
        const targetCode = String(msg.targetCode || '').toUpperCase();
        const target = peers.get(targetCode);
        if (!target) {
          send(ws, { type: 'status', value: 'target_offline' });
          return;
        }
        peerCode = targetCode;
        target.peerCode = code;
        send(ws, { type: 'status', value: 'paired' });
        send(target, { type: 'status', value: 'paired' });
        return;
      }

      if (msg.type === 'message') {
        if (!peerCode) {
          send(ws, { type: 'status', value: 'not_paired' });
          return;
        }
        const target = peers.get(peerCode);
        if (!target) {
          send(ws, { type: 'status', value: 'target_offline' });
          return;
        }
        const text = String(msg.text || '').trim();
        if (!text) return;
        send(target, {
          type: 'message',
          id: String(msg.id || randomUUID()),
          senderId: deviceId,
          text,
          speak: Boolean(msg.speak),
          createdAt: Number(msg.createdAt || Date.now())
        });
        send(ws, { type: 'status', value: 'delivered' });
      }
    } catch {
      send(ws, { type: 'status', value: 'bad_request' });
    }
  });

  ws.on('close', () => {
    if (code && peers.get(code) === ws) {
      peers.delete(code);
      if (peerCode) {
        const peer = peers.get(peerCode);
        if (peer && peer.peerCode === code) {
          peer.peerCode = null;
          send(peer, { type: 'status', value: 'partner_offline' });
        }
      }
    }
  });
});

console.log('AirCall relay listening on ' + port);
