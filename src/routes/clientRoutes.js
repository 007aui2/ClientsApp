const express = require('express');
const router = express.Router();
const ClientController = require('../controllers/clientController');
const auth = require('../middleware/auth');

// Все маршруты требуют аутентификации
router.use(auth.verifyToken);

// Получить всех клиентов пользователя
router.get('/', ClientController.getAll);

// Получить клиента по ID
router.get('/:id', ClientController.getById);

// Создать нового клиента
router.post('/', ClientController.create);

// Обновить клиента
router.put('/:id', ClientController.update);

// Завершить месяц
router.post('/complete-month', ClientController.completeMonth);

// Удалить клиента
router.delete('/:id', ClientController.delete);

module.exports = router;