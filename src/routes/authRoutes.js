const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const AuthController = require('../controllers/authController');
const auth = require('../middleware/auth');

// Валидация для регистрации
const registerValidation = [
    body('username')
        .notEmpty().withMessage('Логин обязателен')
        .isLength({ min: 3 }).withMessage('Логин должен быть не менее 3 символов'),
    body('email')
        .notEmpty().withMessage('Email обязателен')
        .isEmail().withMessage('Введите корректный email'),
    body('password')
        .notEmpty().withMessage('Пароль обязателен')
        .isLength({ min: 6 }).withMessage('Пароль должен быть не менее 6 символов'),
    body('full_name')
        .notEmpty().withMessage('ФИО обязательно')
];

// Валидация для входа
const loginValidation = [
    body('username').notEmpty().withMessage('Логин обязателен'),
    body('password').notEmpty().withMessage('Пароль обязателен')
];

// Регистрация
router.post('/register', registerValidation, AuthController.register);

// Вход
router.post('/login', loginValidation, AuthController.login);

// Получение профиля (требует аутентификации)
router.get('/profile', auth.verifyToken, AuthController.getProfile);

// Обновление профиля (требует аутентификации)
router.put('/profile', auth.verifyToken, AuthController.updateProfile);

module.exports = router;