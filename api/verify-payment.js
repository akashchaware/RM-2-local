const crypto = require('crypto');

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
        const { order_id, payment_id, signature } = req.body;

        // Missing fields validation
        if (!order_id || !payment_id || !signature) {
            return res.status(400).json({ error: 'Missing required validation fields' });
        }

        const keySecret = process.env.RAZORPAY_KEY_SECRET || '9WlupbeE0BIE9tsJXNZFrmVZ';

        if (!keySecret) {
            return res.status(401).json({ error: 'Razorpay secret key is missing on the server' });
        }

        // HMAC-SHA256 signature verification as per Razorpay spec: HMAC-SHA256(order_id + "|" + payment_id, KEY_SECRET)
        const text = order_id + '|' + payment_id;
        const generatedSignature = crypto
            .createHmac('sha256', keySecret)
            .update(text)
            .digest('hex');

        if (generatedSignature === signature) {
            return res.status(200).json({
                success: true,
                message: 'Payment signature verified successfully'
            });
        } else {
            return res.status(400).json({
                success: false,
                error: 'Signature verification failed. Potential tampering or fraud detected.'
            });
        }
    } catch (err) {
        console.error('Razorpay Signature Verification Error:', err);
        return res.status(500).json({ error: err.message || 'Internal Server Error' });
    }
};
