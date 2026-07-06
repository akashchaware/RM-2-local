// ─── homepage.js – Calculator, Auth Modal & Mobile Menu ───

const SUPABASE_URL = 'https://mpcnfrshpgcpmrgledwy.supabase.co';
const SUPABASE_ANON_KEY = 'sb_publishable_IlSzuHbWowZ84IdxRwBCxg_DDT9P_Vz';
const supabase = window.supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY);

let allBrands = [], allDevices = [], allRepairTypes = [], allParts = [];
let currentUser = null;

// ─── TOAST ───
function showToast(msg) {
    const t = document.getElementById('toast');
    if (!t) return;
    t.textContent = msg;
    t.classList.add('show');
    clearTimeout(t._hide);
    t._hide = setTimeout(() => t.classList.remove('show'), 3000);
}

// ─── MOBILE MENU TOGGLE ───
function toggleMobileMenu() {
    const nav = document.getElementById('mainNav');
    if (nav) {
        nav.classList.toggle('open');
    }
}

// ─── LOGIN MODAL ───
function openLoginModal() {
    let modal = document.getElementById('authModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'authModal';
        modal.style.cssText = 'display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.7); z-index:9999; justify-content:center; align-items:center; padding:20px;';
        modal.innerHTML = `
            <div style="background:#1a2a3a; max-width:400px; width:100%; border-radius:20px; padding:24px; max-height:90vh; overflow-y:auto; color:#fff; position:relative;">
                <button onclick="closeLoginModal()" style="position:absolute; top:12px; right:16px; background:none; border:none; font-size:24px; color:#fff; cursor:pointer;">&times;</button>
                <h2 style="font-size:20px; margin-bottom:16px;">Welcome Back</h2>
                <div style="display:flex; gap:6px; background:#0A0F1D; border-radius:10px; padding:4px; margin-bottom:12px;">
                    <button class="auth-tab active" data-tab="login" style="flex:1; padding:10px; border:none; border-radius:8px; font-weight:600; background:#14B8A6; color:#fff; cursor:pointer;">Sign In</button>
                    <button class="auth-tab" data-tab="signup" style="flex:1; padding:10px; border:none; border-radius:8px; font-weight:600; background:transparent; color:#aaa; cursor:pointer;">Sign Up</button>
                </div>
                <form id="loginFormModal">
                    <input type="email" id="loginEmailModal" placeholder="Email" style="width:100%; padding:12px; margin-bottom:10px; border-radius:10px; border:1px solid #2a3a4a; background:#0A0F1D; color:#fff;" required />
                    <input type="password" id="loginPasswordModal" placeholder="Password" style="width:100%; padding:12px; margin-bottom:10px; border-radius:10px; border:1px solid #2a3a4a; background:#0A0F1D; color:#fff;" required />
                    <button type="submit" style="width:100%; padding:14px; background:#14B8A6; color:#0A0F1D; border:none; border-radius:10px; font-weight:700; cursor:pointer;">Sign In</button>
                </form>
                <form id="signupFormModal" style="display:none;">
                    <input type="text" id="signupNameModal" placeholder="Full Name" style="width:100%; padding:12px; margin-bottom:10px; border-radius:10px; border:1px solid #2a3a4a; background:#0A0F1D; color:#fff;" required />
                    <input type="email" id="signupEmailModal" placeholder="Email" style="width:100%; padding:12px; margin-bottom:10px; border-radius:10px; border:1px solid #2a3a4a; background:#0A0F1D; color:#fff;" required />
                    <input type="tel" id="signupPhoneModal" placeholder="Phone Number" style="width:100%; padding:12px; margin-bottom:10px; border-radius:10px; border:1px solid #2a3a4a; background:#0A0F1D; color:#fff;" />
                    <input type="password" id="signupPasswordModal" placeholder="Password" style="width:100%; padding:12px; margin-bottom:10px; border-radius:10px; border:1px solid #2a3a4a; background:#0A0F1D; color:#fff;" required />
                    <button type="submit" style="width:100%; padding:14px; background:#14B8A6; color:#0A0F1D; border:none; border-radius:10px; font-weight:700; cursor:pointer;">Create Account</button>
                </form>
                <div style="margin-top:12px; text-align:center;">
                    <button onclick="signInWithGoogle()" style="background:none; border:none; color:#14B8A6; font-weight:600; cursor:pointer;">
                        <i class="fa-brands fa-google"></i> Sign in with Google
                    </button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);

        // Tab switching inside modal
        modal.querySelectorAll('.auth-tab').forEach(btn => {
            btn.addEventListener('click', function() {
                modal.querySelectorAll('.auth-tab').forEach(b => {
                    b.style.background = b === this ? '#14B8A6' : 'transparent';
                    b.style.color = b === this ? '#fff' : '#aaa';
                });
                document.getElementById('loginFormModal').style.display = this.dataset.tab === 'login' ? 'block' : 'none';
                document.getElementById('signupFormModal').style.display = this.dataset.tab === 'signup' ? 'block' : 'none';
            });
        });

        // Login handler
        document.getElementById('loginFormModal').addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('loginEmailModal').value;
            const password = document.getElementById('loginPasswordModal').value;
            const { data, error } = await supabase.auth.signInWithPassword({ email, password });
            if (error) return showToast('Login failed: ' + error.message);
            showToast('Welcome!');
            closeLoginModal();
            setTimeout(() => { window.location.href = 'dashboard.html'; }, 800);
        });

        // Signup handler
        document.getElementById('signupFormModal').addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('signupNameModal').value;
            const email = document.getElementById('signupEmailModal').value;
            const phone = document.getElementById('signupPhoneModal').value;
            const password = document.getElementById('signupPasswordModal').value;
            const { data, error } = await supabase.auth.signUp({
                email, password,
                options: { data: { full_name: name, phone } }
            });
            if (error) return showToast('Signup failed: ' + error.message);
            if (data.user) {
                await supabase.from('user_roles').upsert([{ user_id: data.user.id, role_id: 5 }]);
            }
            showToast('Account created! Please sign in.');
            modal.querySelector('.auth-tab[data-tab="login"]').click();
        });
    }
    modal.style.display = 'flex';
}

function closeLoginModal() {
    const modal = document.getElementById('authModal');
    if (modal) modal.style.display = 'none';
}

window.signInWithGoogle = async function() {
    await supabase.auth.signInWithOAuth({ provider: 'google' });
};

// ─── CATALOG LOADER ───
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

// ─── INIT ───
document.addEventListener('DOMContentLoaded', function() {
    loadCatalog();

    // Attach listeners
    const qualitySelect = document.getElementById('qualitySelect');
    const offerToggle = document.getElementById('offerToggle');
    const modelSelect = document.getElementById('modelSelect');
    const repairSelect = document.getElementById('repairTypeSelect');
    const brandSelect = document.getElementById('brandSelect');

    if (qualitySelect) qualitySelect.addEventListener('change', updatePartsSurvey);
    if (offerToggle) offerToggle.addEventListener('change', updatePartsSurvey);
    if (modelSelect) modelSelect.addEventListener('change', updateRepairTypes);
    if (repairSelect) repairSelect.addEventListener('change', updatePartsSurvey);
    if (brandSelect) {
        brandSelect.removeEventListener('change', updateModels);
        brandSelect.addEventListener('change', updateModels);
    }

    // Add login buttons to mobile menu
    const mobileNav = document.getElementById('mainNav');
    if (mobileNav && !mobileNav.querySelector('.mobile-login-links')) {
        const loginLinks = document.createElement('div');
        loginLinks.className = 'mobile-login-links';
        loginLinks.style.cssText = 'display:flex; gap:16px; margin-top:8px; border-top:1px solid rgba(255,255,255,0.05); padding-top:12px;';
        loginLinks.innerHTML = `
            <a href="#" onclick="openLoginModal(); return false;" style="color:#14B8A6; font-weight:600; text-decoration:none;">Login</a>
            <a href="#" onclick="openLoginModal(); return false;" style="color:rgba(255,255,255,0.65); text-decoration:none;">Sign Up</a>
        `;
        mobileNav.appendChild(loginLinks);
    }
});

// ─── Expose functions globally ───
window.toggleMobileMenu = toggleMobileMenu;
window.openLoginModal = openLoginModal;
window.closeLoginModal = closeLoginModal;
window.signInWithGoogle = signInWithGoogle;
window.updateModels = updateModels;
window.updateRepairTypes = updateRepairTypes;
window.updatePartsSurvey = updatePartsSurvey;
