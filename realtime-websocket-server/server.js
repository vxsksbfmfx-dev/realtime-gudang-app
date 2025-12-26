const WebSocket = require('ws');
const http = require('http');

// Create HTTP server
const server = http.createServer();

// Create WebSocket server
const wss = new WebSocket.Server({ server });

// Store all connected clients
const clients = new Set();

// Handle WebSocket connections
wss.on('connection', (ws) => {
    console.log('New client connected');
    clients.add(ws);

    // Handle client messages (optional)
    ws.on('message', (message) => {
        console.log('Received:', message.toString());
    });

    // Handle client disconnect
    ws.on('close', () => {
        console.log('Client disconnected');
        clients.delete(ws);
    });

    // Handle errors
    ws.on('error', (error) => {
        console.error('WebSocket error:', error);
        clients.delete(ws);
    });
});

// Handle HTTP POST requests for notifications
server.on('request', (req, res) => {
    // Set CORS headers
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

    // Handle preflight requests
    if (req.method === 'OPTIONS') {
        res.writeHead(200);
        res.end();
        return;
    }

    if (req.method === 'POST' && req.url === '/notify') {
        let body = '';

        req.on('data', chunk => {
            body += chunk.toString();
        });

        req.on('end', () => {
            try {
                const message = JSON.parse(body);
                console.log('Broadcasting message:', message);

                // Broadcast to all connected clients
                let sentCount = 0;
                clients.forEach(client => {
                    if (client.readyState === WebSocket.OPEN) {
                        client.send(JSON.stringify(message));
                        sentCount++;
                    }
                });

                console.log(`Message sent to ${sentCount} clients`);

                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: true,
                    clientCount: sentCount
                }));
            } catch (error) {
                console.error('Error processing notification:', error);
                res.writeHead(400, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({
                    success: false,
                    error: error.message
                }));
            }
        });
    } else {
        res.writeHead(404);
        res.end('Not Found');
    }
});

// Start server
const PORT = 3000;
server.listen(PORT, () => {
    console.log(`WebSocket server is running on port ${PORT}`);
    console.log(`WebSocket: ws://localhost:${PORT}`);
    console.log(`HTTP Notify: http://localhost:${PORT}/notify`);
});

// Handle server errors
server.on('error', (error) => {
    console.error('Server error:', error);
});

// Graceful shutdown
process.on('SIGTERM', () => {
    console.log('SIGTERM received, closing server...');
    server.close(() => {
        console.log('Server closed');
        process.exit(0);
    });
});