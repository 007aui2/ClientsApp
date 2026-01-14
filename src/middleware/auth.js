const jwt = require('jsonwebtoken');
const UserModel = require('../models/userModel');

const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key-change-in-production';

const authMiddleware = {
    // Генерация токена
    generateToken: (user) => {
        return jwt.sign(
            { id: user.id, username: user.username, role: user.role },
            JWT_SECRET,
            { expiresIn: '7d' }
        );
    },

    // Middleware для проверки токена
    verifyToken: async (req, res, next) => {
        try {
            const token = req.headers.authorization?.split(' ')[1];
            
            if (!token) {
                return res.status(401).json({ error: 'No token provided' });
            }

            const decoded = jwt.verify(token, JWT_SECRET);
            const user = await UserModel.findById(decoded.id);
            
            if (!user) {
                return res.status(401).json({ error: 'User not found' });
            }

            req.user = user;
            next();
        } catch (error) {
            if (error.name === 'JsonWebTokenError') {
                return res.status(401).json({ error: 'Invalid token' });
            }
            if (error.name === 'TokenExpiredError') {
                return res.status(401).json({ error: 'Token expired' });
            }
            console.error('Auth error:', error);
            res.status(500).json({ error: 'Authentication failed' });
        }
    },

    // Middleware для проверки ролей
    requireRole: (...roles) => {
        return (req, res, next) => {
            if (!req.user) {
                return res.status(401).json({ error: 'Authentication required' });
            }
            
            if (!roles.includes(req.user.role)) {
                return res.status(403).json({ error: 'Insufficient permissions' });
            }
            
            next();
        };
    }
};

module.exports = authMiddleware;