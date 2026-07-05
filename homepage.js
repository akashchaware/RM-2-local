// ─── homepage.js – Calculator logic for the homepage ───

const SUPABASE_URL = 'https://mpcnfrshpgcpmrgledwy.supabase.co';
const SUPABASE_ANON_KEY = 'sb_publishable_IlSzuHbWowZ84IdxRwBCxg_DDT9P_Vz';
const supabase = window.supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY);

let allBrands = [], allDevices = [], allRepairTypes = [], allParts = [];

async function loadCatalog() {
    try {
        const [b, d, rt, p] = await Promise.all([
            supabase.from('brands').select('*').order('name'),
            supabase.from('devices').select('*').order('name'),
            supabase.from('repair_types').select('*').order('label'),
            supabase.from('parts').select('*')
        ]);
        if (b.error) throw new Error('Brands error');
        allBrands = b.data || [];
        allDevices = d.data || [];
        allRepairTypes = rt.data || [];
        allParts = p.data || [];

        if (allBrands.length === 0) useFallback();
        else populateBrands();
    } catch (e) {
        console.warn('Supabase catalog failed, using fallback.');
        useFallback();
    }
}

function useFallback() {
    allBrands = [
        { id: 'b1', name: 'Apple' }, { id: 'b2', name: 'Samsung' },
        { id: 'b3', name: 'Vivo' }, { id: 'b4', name: 'OnePlus' },
        { id: 'b5', name: 'Xiaomi' }, { id: 'b6', name: 'Oppo' }
    ];
    allDevices = [
        { id: 'd1', brand_id: 'b1', name: 'iPhone 15 Pro' },
        { id: 'd2', brand_id: 'b1', name: 'iPhone 14' },
        { id: 'd3', brand_id: 'b2', name: 'Galaxy S24 Ultra' },
        { id: 'd4', brand_id: 'b2', name: 'Galaxy A55' },
        { id: 'd5', brand_id: 'b3', name: 'V30 Pro' },
        { id: 'd6', brand_id: 'b3', name: 'V29' }
    ];
    allRepairTypes = [
        { id: 'rt1', name: 'screen', label: 'Screen Replacement' },
        { id: 'rt2', name: 'battery', label: 'Battery Replacement' },
        { id: 'rt3', name: 'chargingport', label: 'Charging Port' }
    ];
    allParts = [
        { device_id: 'd5', repair_type_id: 'rt1', name: 'AMOLED Screen Panel Assembly', price: 6300 },
        { device_id: 'd5', repair_type_id: 'rt2', name: 'Li-Po Battery', price: 1500 },
        { device_id: 'd1', repair_type_id: 'rt1', name: 'OLED Display', price: 15000 },
        { device_id: 'd1', repair_type_id: 'rt2', name: 'Battery', price: 4500 }
    ];
    populateBrands();
}

function populateBrands() {
    const sel = document.getElementById('brandSelect');
    if (!sel) return;
    sel.innerHTML = '<option value="">— Select Brand —</option>';
    allBrands.forEach(b => {
        const opt = document.createElement('option');
        opt.value = b.id;
        opt.textContent = b.name;
        sel.appendChild(opt);
    });
    sel.addEventListener('change', updateModels);
    updateModels();
}

function updateModels() {
    const brandId = document.getElementById('brandSelect').value;
    const sel = document.getElementById('modelSelect');
    if (!sel) return;
    sel.innerHTML = '<option value="">— Select Model —</option>';
    if (!brandId) { updateRepairTypes(); return; }
    allDevices.filter(d => d.brand_id === brandId).forEach(d => {
        const opt = document.createElement('option');
        opt.value = d.id;
        opt.textContent = d.name;
        sel.appendChild(opt);
    });
    updateRepairTypes();
}

function updateRepairTypes() {
    const sel = document.getElementById('repairTypeSelect');
    if (!sel) return;
    sel.innerHTML = '<option value="">— Select Repair —</option>';
    allRepairTypes.forEach(rt => {
        const opt = document.createElement('option');
        opt.value = rt.id;
        opt.textContent = rt.label || rt.name;
        sel.appendChild(opt);
    });
    updatePartsSurvey();
}

function updatePartsSurvey() {
    const modelId = document.getElementById('modelSelect').value;
    const repairId = document.getElementById('repairTypeSelect').value;
    const quality = document.getElementById('qualitySelect').value;
    const offer = document.getElementById('offerToggle').checked;
    const parts = allParts.filter(p => p.device_id === modelId && p.repair_type_id === repairId);
    const qm = quality === 'premium' ? 1 : 0.7;
    const container = document.getElementById('partsSurveyContainer');

    if (!modelId || !repairId || parts.length === 0) {
        if (container) container.innerHTML = 'Select brand, model & repair to see parts.';
        document.getElementById('partsTotalDisplay').textContent = '₹0.00';
        document.getElementById('serviceFeeDisplay').textContent = '₹0.00';
        document.getElementById('totalPriceDisplay').textContent = '₹0.00';
        return;
    }
    let total = parts.reduce((s, p) => s + p.price * qm, 0);
    const discounted = total * 0.9;
    let service = discounted * 0.15;
    if (offer) service *= 0.5;
    const diagnosis = 250;
    const final = discounted + service + diagnosis;

    if (container) {
        container.innerHTML = parts.map(p => `<div style="display:flex;justify-content:space-between;padding:4px 0;border-bottom:1px solid rgba(255,255,255,0.05);"><span>${p.name}</span><span>₹${(p.price * qm).toFixed(0)}</span></div>`).join('');
    }
    document.getElementById('partsTotalDisplay').textContent = '₹' + discounted.toFixed(0);
    document.getElementById('serviceFeeDisplay').textContent = '₹' + service.toFixed(0);
    document.getElementById('totalPriceDisplay').textContent = '₹' + final.toFixed(0);
}

// ─── Initialize when DOM is ready ───
document.addEventListener('DOMContentLoaded', function() {
    // Load catalog
    loadCatalog();

    // Attach listeners to dropdowns
    const qualitySelect = document.getElementById('qualitySelect');
    const offerToggle = document.getElementById('offerToggle');
    const modelSelect = document.getElementById('modelSelect');
    const repairSelect = document.getElementById('repairTypeSelect');

    if (qualitySelect) qualitySelect.addEventListener('change', updatePartsSurvey);
    if (offerToggle) offerToggle.addEventListener('change', updatePartsSurvey);
    if (modelSelect) modelSelect.addEventListener('change', updateRepairTypes);
    if (repairSelect) repairSelect.addEventListener('change', updatePartsSurvey);

    // brandSelect may have inline onchange, but we'll override it anyway
    const brandSelect = document.getElementById('brandSelect');
    if (brandSelect) {
        brandSelect.removeEventListener('change', updateModels);
        brandSelect.addEventListener('change', updateModels);
    }
});
