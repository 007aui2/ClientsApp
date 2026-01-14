const pool = require('../config/database');
const bcrypt = require('bcrypt');

class UserModel {
    static async create(username, email, password, full_name, role = 'specialist') {
        const passwordHash = await bcrypt.hash(password, 10);
        const result = await pool.query(
            'INSERT INTO users (username, email, password_hash, full_name, role) VALUES ($1, $2, $3, $4, $5) RETURNING *',
            [username, email, passwordHash, full_name, role]
        );
        return result.rows[0];
    }

    static async findByUsername(username) {
        const result = await pool.query('SELECT * FROM users WHERE username = $1', [username]);
        return result.rows[0];
    }

    static async findByEmail(email) {
        const result = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
        return result.rows[0];
    }

    static async findById(id) {
        const result = await pool.query('SELECT id, username, email, full_name, role FROM users WHERE id = $1', [id]);
        return result.rows[0];
    }

    static async verifyPassword(user, password) {
        return await bcrypt.compare(password, user.password_hash);
    }

    static async update(id, updates) {
        const { email, full_name } = updates;
        const result = await pool.query(
            'UPDATE users SET email = $1, full_name = $2, updated_at = CURRENT_TIMESTAMP WHERE id = $3 RETURNING id, username, email, full_name, role',
            [email, full_name, id]
        );
        return result.rows[0];
    }
}

module.exports = UserModel;