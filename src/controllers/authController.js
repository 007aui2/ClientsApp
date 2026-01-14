const UserModel = require('../models/userModel');
const authMiddleware = require('../middleware/auth');
const { validationResult } = require('express-validator');

class AuthController {
    static async register(req, res) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                return res.status(400).json({ errors: errors.array() });
            }

            const { username, email, password, full_name } = req.body;

            // Проверяем, существует ли пользователь
            const existingUser = await UserModel.findByUsername(username);
            if (existingUser) {
                return res.status(400).json({ error: 'Пользователь с таким логином уже существует' });
            }

            const existingEmail = await UserModel.findByEmail(email);
            if (existingEmail) {
                return res.status(400).json({ error: 'Пользователь с таким email уже существует' });
            }

            // Создаем пользователя
            const user = await UserModel.create(username, email, password, full_name);

            // Генерируем токен
            const token = authMiddleware.generateToken(user);

            res.status(201).json({
                message: 'Регистрация успешна',
                token,
                user: {
                    id: user.id,
                    username: user.username,
                    email: user.email,
                    full_name: user.full_name,
                    role: user.role
                }
            });
        } catch (error) {
            console.error('Error in register:', error);
            res.status(500).json({ error: 'Ошибка при регистрации' });
        }
    }

    static async login(req, res) {
        try {
            const { username, password } = req.body;

            // Находим пользователя
            const user = await UserModel.findByUsername(username);
            if (!user) {
                return res.status(401).json({ error: 'Неверный логин или пароль' });
            }

            // Проверяем пароль
            const isValidPassword = await UserModel.verifyPassword(user, password);
            if (!isValidPassword) {
                return res.status(401).json({ error: 'Неверный логин или пароль' });
            }

            // Генерируем токен
            const token = authMiddleware.generateToken(user);

            res.json({
                message: 'Вход выполнен успешно',
                token,
                user: {
                    id: user.id,
                    username: user.username,
                    email: user.email,
                    full_name: user.full_name,
                    role: user.role
                }
            });
        } catch (error) {
            console.error('Error in login:', error);
            res.status(500).json({ error: 'Ошибка при входе' });
        }
    }

    static async getProfile(req, res) {
        try {
            res.json({
                user: req.user
            });
        } catch (error) {
            console.error('Error in getProfile:', error);
            res.status(500).json({ error: 'Ошибка при получении профиля' });
        }
    }

    static async updateProfile(req, res) {
        try {
            const { email, full_name } = req.body;
            const updatedUser = await UserModel.update(req.user.id, { email, full_name });
            res.json({
                message: 'Профиль обновлен',
                user: updatedUser
            });
        } catch (error) {
            console.error('Error in updateProfile:', error);
            res.status(500).json({ error: 'Ошибка при обновлении профиля' });
        }
    }
}

module.exports = AuthController;