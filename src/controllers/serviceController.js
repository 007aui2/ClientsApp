const ServiceModel = require('../models/serviceModel');
const ClientModel = require('../models/clientModel');

class ServiceController {
    static async getAll(req, res) {
        try {
            const services = await ServiceModel.getAll();
            res.json(services);
        } catch (error) {
            console.error('Error in getAll services:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }

    static async getByClientId(req, res) {
        try {
            const { clientId } = req.params;
            
            // Проверяем, принадлежит ли клиент пользователю
            const client = await ClientModel.findById(clientId, req.user.id);
            if (!client) {
                return res.status(404).json({ error: 'Client not found or access denied' });
            }

            const services = await ServiceModel.getByClientId(clientId);
            res.json(services);
        } catch (error) {
            console.error('Error in getByClientId:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }

    static async addToClient(req, res) {
        try {
            const { clientId, serviceId } = req.params;
            
            // Проверяем, принадлежит ли клиент пользователю
            const client = await ClientModel.findById(clientId, req.user.id);
            if (!client) {
                return res.status(404).json({ error: 'Client not found or access denied' });
            }

            const result = await ServiceModel.addServiceToClient(clientId, serviceId);
            res.json(result);
        } catch (error) {
            console.error('Error in addToClient:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }

    static async removeFromClient(req, res) {
        try {
            const { clientId, serviceId } = req.params;
            
            // Проверяем, принадлежит ли клиент пользователю
            const client = await ClientModel.findById(clientId, req.user.id);
            if (!client) {
                return res.status(404).json({ error: 'Client not found or access denied' });
            }

            await ServiceModel.removeServiceFromClient(clientId, serviceId);
            res.status(204).send();
        } catch (error) {
            console.error('Error in removeFromClient:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }
}

module.exports = ServiceController;