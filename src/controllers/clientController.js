const ClientModel = require('../models/clientModel');

class ClientController {
    static async getAll(req, res) {
        try {
            console.log('GET /api/clients - Fetching all clients for user:', req.user.id);
            const showCompleted = req.query.showCompleted !== 'false';
            console.log(`Show completed: ${showCompleted}`);
            
            const clients = await ClientModel.getAll(req.user.id, showCompleted);
            console.log(`Found ${clients.length} clients`);
            
            res.json(clients);
        } catch (error) {
            console.error('❌ Error in getAll:', error);
            res.status(500).json({ 
                error: 'Internal server error',
                details: error.message 
            });
        }
    }

    static async getById(req, res) {
        try {
            const { id } = req.params;
            const client = await ClientModel.findById(id, req.user.id);
            
            if (!client) {
                return res.status(404).json({ 
                    error: 'Not found',
                    message: 'Client not found' 
                });
            }
            
            res.json(client);
        } catch (error) {
            console.error('❌ Error in getById:', error);
            res.status(500).json({ 
                error: 'Internal server error',
                details: error.message 
            });
        }
    }

    static async create(req, res) {
        try {
            console.log('POST /api/clients - Creating new client for user:', req.user.id);
            console.log('Request body:', req.body);
            
            const { client_name } = req.body;
            
            if (!client_name || client_name.trim() === '') {
                console.log('Validation failed: client name is empty');
                return res.status(400).json({ 
                    error: 'Validation failed',
                    message: 'Client name is required' 
                });
            }
            
            const client = await ClientModel.create(req.user.id, client_name.trim());
            console.log('Client created successfully:', client);
            
            res.status(201).json(client);
        } catch (error) {
            console.error('❌ Error in create:', error);
            res.status(500).json({ 
                error: 'Internal server error',
                details: error.message
            });
        }
    }

    static async update(req, res) {
    try {
        const { id } = req.params;
        const updates = req.body;
        console.log('Updates received:', JSON.stringify(updates, null, 2));
        
        console.log(`PUT /api/clients/${id} - Updating client for user:`, req.user.id);
        
        const cleanUpdates = {};
        Object.keys(updates).forEach(key => {
            if (updates[key] !== undefined) {
                cleanUpdates[key] = updates[key];
            }
        });
        
        console.log('Clean updates:', JSON.stringify(cleanUpdates, null, 2));
        
        const client = await ClientModel.update(id, req.user.id, updates);
        
        if (!client) {
            console.log(`Client ${id} not found or access denied`);
            return res.status(404).json({ 
                error: 'Not found',
                message: 'Client not found' 
            });
        }
        
        console.log(`Client ${id} updated successfully:`, client.client_name);
        res.json(client);
    } catch (error) {
        console.error('❌ Error in update:', error);
        res.status(500).json({ 
            error: 'Internal server error',
            details: error.message 
        });
    }
}

    static async completeMonth(req, res) {
        try {
            console.log('POST /api/clients/complete-month - Completing month for user:', req.user.id);
            
            const result = await ClientModel.completeMonth(req.user.id);
            console.log('Month completed successfully');
            
            res.json(result);
        } catch (error) {
            console.error('❌ Error in completeMonth:', error);
            res.status(500).json({ 
                error: 'Internal server error',
                details: error.message 
            });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            console.log(`DELETE /api/clients/${id} - Deleting client for user:`, req.user.id);
            
            await ClientModel.delete(id, req.user.id);
            console.log(`Client ${id} deleted successfully`);
            
            res.status(204).send();
        } catch (error) {
            console.error('❌ Error in delete:', error);
            res.status(500).json({ 
                error: 'Internal server error',
                details: error.message 
            });
        }
    }
}

module.exports = ClientController;