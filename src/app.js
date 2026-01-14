const express = require('express');
const cors = require('cors');
require('dotenv').config();

const clientRoutes = require('./routes/clientRoutes');
const authRoutes = require('./routes/authRoutes');
const serviceRoutes = require('./routes/serviceRoutes');

const app = express();
const PORT = process.env.PORT || 5000;
const HOST = process.env.HOST || '0.0.0.0';
// Middleware
app.use(cors({
    origin: "*",
    credentials: true
}));
app.use(express.json());

// Routes
app.use('/api/clients', clientRoutes);
app.use('/api/auth', authRoutes);
app.use('/api/services', serviceRoutes);

// Health check
app.get('/health', (req, res) => {
    res.json({ 
        status: 'OK', 
        timestamp: new Date().toISOString(),
        message: 'Client Monitoring API is running'
    });
});

// Test route
app.get('/api/test', (req, res) => {
    res.json({ message: 'API is working!' });
});

app.get('/api/auth/test', (req, res) => {
    res.json({ message: 'Auth routes are working!' });
});

// Error handling middleware
app.use((err, req, res, next) => {
    console.error('Server error:', err.stack);
    res.status(500).json({ 
        error: 'Internal server error',
        message: err.message 
    });
});

// 404 handler - ДОЛЖЕН БЫТЬ ПОСЛЕ ВСЕХ МАРШРУТОВ
app.use('*', (req, res) => {
    console.log('❌ Route not found:', req.method, req.originalUrl);
    res.status(404).json({ 
        error: 'Route not found',
        requested: `${req.method} ${req.originalUrl}`,
        available: [
            'GET /health',
            'GET /api/test',
            'GET /api/auth/test',
            'POST /api/auth/register',
            'POST /api/auth/login',
            'GET /api/auth/profile',
            'GET /api/clients',
            'GET /api/services'
        ]
    });
});

// Start server
app.listen(PORT,HOST, () => {
    console.log(`✅ Server is running on http://${HOST}:${PORT}`);
    console.log(`🔗 Health check: http://${HOST}:${PORT}/health`);
    console.log(`🌐 CORS enabled for: http://localhost:5173`);
});