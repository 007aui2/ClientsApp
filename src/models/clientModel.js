const pool = require('../config/database');

class ClientModel {
    static async getAll(userId, showCompleted = true) {
    try {
        let query = `
           SELECT 
                c.*, 
                COALESCE(
                    json_agg(DISTINCT s.service_name) FILTER (WHERE s.id IS NOT NULL), 
                    '[]'::json
                ) as services,
                COALESCE(
                    json_agg(DISTINCT s.id) FILTER (WHERE s.id IS NOT NULL), 
                    '[]'::json
                ) as service_ids
            FROM clients c
            LEFT JOIN client_services cs ON c.id = cs.client_id
            LEFT JOIN services s ON cs.service_id = s.id
            WHERE c.user_id = $1 `;
        
        const params = [userId];
        
        if (!showCompleted) {
            query += ' AND c.is_completed = $2';
            params.push(false);
        }
        
        query += ' GROUP BY c.id ORDER BY c.planned_date ASC, c.id ASC';
        
        const result = await pool.query(query, params);
        
        // Преобразуем JSON массивы в обычные массивы
        const clients = result.rows.map(row => ({
            ...row,
            services: row.services || [],
            service_ids: row.service_ids || []
        }));
        
        return clients;
    } catch (error) {
        console.error('Error in getAll:', error);
        throw error;
    }
}

    static async create(userId, clientName) {
    try {
        const result = await pool.query(
            'INSERT INTO clients (client_name, user_id, notes) VALUES ($1, $2, $3) RETURNING *',
            [clientName, userId, '']  // Пустая заметка по умолчанию
        );
        return result.rows[0];
    } catch (error) {
        throw error;
    }
}


   static async update(id, userId, updates) {
    try {
         const { 
            planned_date, 
            is_completed, 
            is_lurv_sent, 
            services, 
            phone,        
            email,        
            notes         
        } = updates;
        
        // Проверяем, принадлежит ли клиент пользователю
        const check = await pool.query(
            'SELECT id, planned_date FROM clients WHERE id = $1 AND user_id = $2',
            [id, userId]
        );
        
        if (check.rows.length === 0) {
            throw new Error('Client not found or access denied');
        }

        // Логируем для отладки
        console.log('ClientModel.update - received planned_date:', planned_date);
        console.log('ClientModel.update - current planned_date in DB:', check.rows[0].planned_date);

        // Строим динамический запрос, чтобы обрабатывать null правильно
        const updateFields = [];
        const values = [];
        let paramCount = 1;

        if (updates.hasOwnProperty('planned_date')) {
            // Если передано явно null, устанавливаем NULL
            // Если передана дата, устанавливаем дату
            // Если не передано, не обновляем поле
            updateFields.push(`planned_date = $${paramCount}`);
            values.push(planned_date); // Может быть null или датой
            paramCount++;
        }

        if (updates.hasOwnProperty('is_completed')) {
            updateFields.push(`is_completed = $${paramCount}`);
            values.push(is_completed);
            paramCount++;
        }

        if (updates.hasOwnProperty('is_lurv_sent')) {
            updateFields.push(`is_lurv_sent = $${paramCount}`);
            values.push(is_lurv_sent);
            paramCount++;
        }
        if (updates.hasOwnProperty('notes')) {
            updateFields.push(`notes = $${paramCount}`);
            values.push(notes || '');
            paramCount++;
        }
        if (updates.hasOwnProperty('phone')) {
            updateFields.push(`phone = $${paramCount}`);
            values.push(phone);
            paramCount++;
}

        if (updates.hasOwnProperty('email')) {
            updateFields.push(`email = $${paramCount}`);
            values.push(email);
            paramCount++;
}

        if (updates.hasOwnProperty('notes')) {
            updateFields.push(`notes = $${paramCount}`);
            values.push(notes);
            paramCount++;
}
        // Если нет полей для обновления, возвращаем текущего клиента
        if (updateFields.length === 0) {
            return check.rows[0];
        }

        // Добавляем updated_at
        updateFields.push('updated_at = CURRENT_TIMESTAMP');

        // Добавляем условия WHERE
        values.push(id, userId);

        const query = `
            UPDATE clients 
            SET ${updateFields.join(', ')}
            WHERE id = $${paramCount} AND user_id = $${paramCount + 1}
            RETURNING *
        `;

        console.log('ClientModel.update - query:', query);
        console.log('ClientModel.update - values:', values);

        const result = await pool.query(query, values);

        // Обновляем сервисы если переданы
        if (services && Array.isArray(services)) {
            const serviceModel = require('./serviceModel');
            await serviceModel.updateClientServices(id, services);
        }

        return result.rows[0];
    } catch (error) {
        console.error('ClientModel.update error:', error);
        throw error;
    }
}

    static async completeMonth(userId) {
        try {
            // Сохраняем текущую дату в previous_month_date
            await pool.query(
                `UPDATE clients SET 
                    previous_month_date = planned_date,
                    planned_date = NULL,
                    is_completed = false,
                    is_lurv_sent = false,
                    updated_at = CURRENT_TIMESTAMP
                 WHERE user_id = $1 AND is_completed = true`,
                [userId]
            );
            return { message: 'Month completed successfully' };
        } catch (error) {
            throw error;
        }
    }

    static async delete(id, userId) {
        try {
            // Проверяем принадлежность клиента
            const check = await pool.query(
                'SELECT id FROM clients WHERE id = $1 AND user_id = $2',
                [id, userId]
            );
            
            if (check.rows.length === 0) {
                throw new Error('Client not found or access denied');
            }

            await pool.query('DELETE FROM clients WHERE id = $1', [id]);
        } catch (error) {
            throw error;
        }
    }

    static async findById(id, userId) {
    try {
        const result = await pool.query(
            `SELECT c.*, 
                   COALESCE(array_agg(s.service_name), '{}') as services,
                   COALESCE(array_agg(s.id), '{}') as service_ids
            FROM clients c
            LEFT JOIN client_services cs ON c.id = cs.client_id
            LEFT JOIN services s ON cs.service_id = s.id
            WHERE c.id = $1 AND c.user_id = $2
            GROUP BY c.id`,
            [id, userId]
        );
        return result.rows[0];
    } catch (error) {
        throw error;
    }
    }
}

module.exports = ClientModel;