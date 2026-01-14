const express = require('express');
const router = express.Router();
const ServiceController = require('../controllers/serviceController');
const auth = require('../middleware/auth');

// Все маршруты требуют аутентификации
router.use(auth.verifyToken);

// Получить все сервисы
router.get('/', ServiceController.getAll);

// Получить сервисы клиента
router.get('/client/:clientId', ServiceController.getByClientId);

// Добавить сервис клиенту
router.post('/client/:clientId/:serviceId', ServiceController.addToClient);

// Удалить сервис у клиента
router.delete('/client/:clientId/:serviceId', ServiceController.removeFromClient);

module.exports = router;