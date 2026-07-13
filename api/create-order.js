const Razorpay = require('razorpay');

module.exports = async (req, res) => {
    // Enable CORS
    res.setHeader('Access-Control-Allow-Credentials', true);
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET,OPTIONS,PATCH,DELETE,POST,PUT');
    res.setHeader('Access-Control-Allow-Headers', 'X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version');

    if (req.method === 'OPTIONS') {
        res.status(200).end();
        return;
    }

    if (req.method !== 'POST') {
        return res.status(405).json({ error: 'Method not allowed' });
    }

    try {
        const { amount, currency, receipt } = req.body;

        // Validation
        if (!amount || amount < 100) {
            return res.status(400).json({ error: 'Amount must be at least 100 paise (₹1)' });
        }

        const keyId = process.env.RAZORPAY_KEY_ID || 'rzp_test_TD0dBmhCyggEFr';
        const keySecret = process.env.RAZORPAY_KEY_SECRET || '9WlupbeE0BIE9tsJXNZFrmVZ';

        if (!keyId || !keySecret) {
            return res.status(401).json({ error: 'Razorpay keys are missing on the server' });
        }

        const instance = new Razorpay({
            key_id: keyId,
            key_secret: keySecret
        });

        const order = await instance.orders.create({
            amount: parseInt(amount, 10),
            currency: currency || 'INR',
            receipt: receipt || `receipt_${Date.now()}`
        });

        return res.status(200).json({
            order_id: order.id,
            amount: order.amount,
            currency: order.currency
        });
    } catch (err) {
        console.error('Razorpay Order Creation Error:', err);
        return res.status(500).json({ error: err.message || 'Internal Server Error' });
    }
};
