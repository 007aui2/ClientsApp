const pool = require('../config/database');

class ServiceModel {
    static async getAll() {
        const result = await pool.query('SELECT * FROM services ORDER BY service_name');
        return result.rows;
    }

    static async getByClientId(clientId) {
        const result = await pool.query(
            `SELECT s.* FROM services s
             JOIN client_services cs ON s.id = cs.service_id
             WHERE cs.client_id = $1
             ORDER BY s.service_name`,
            [clientId]
        );
        return result.rows;
    }

    static async addServiceToClient(clientId, serviceId) {
        const result = await pool.query(
            'INSERT INTO client_services (client_id, service_id) VALUES ($1, $2) RETURNING *',
            [clientId, serviceId]
        );
        return result.rows[0];
    }

    static async removeServiceFromClient(clientId, serviceId) {
        await pool.query(
            'DELETE FROM client_services WHERE client_id = $1 AND service_id = $2',
            [clientId, serviceId]
        );
    }

    static async updateClientServices(clientId, serviceIds) {
        // Удаляем старые связи
        await pool.query('DELETE FROM client_services WHERE client_id = $1', [clientId]);
        
        // Добавляем новые связи
        for (const serviceId of serviceIds) {
            await pool.query(
                'INSERT INTO client_services (client_id, service_id) VALUES ($1, $2)',
                [clientId, serviceId]
            );
        }
    }
}

module.exports = ServiceModel;