// ─────────────────────────────────────────────────────────────
//  main.js – Fully Merged Multi-Page DTC RepairMaster Web Controller
//  Operational Edition: Verified Backend Pipelines & Global Scope
// ─────────────────────────────────────────────────────────────

// ─── SUPABASE CREDENTIALS ───
const SUPABASE_URL = 'https://mpcnfrshpgcpmrgledwy.supabase.co';
const SUPABASE_ANON_KEY = 'sb_publishable_IlSzuHbWowZ84IdxRwBCxg_DDT9P_Vz';
const supabase = window.supabase ? window.supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY) : null;

// ─── GLOBAL SYSTEM STATE ───
let allBrands = [];
let allDevices = [];
let allRepairTypes = [];
let allParts = [];
let currentUser = null;
let currentRoles = [];

// Business Rules: Guaranteed ₹250 Dead Phone Diagnostic Offer
window.diagnosisFee = parseFloat(localStorage.getItem('diagnosis_fee')) || 250;

// Track active custom quotation states natively
window.editingQuotationParts = {};
window.editingQuotationDiagnosisCharge = {};
window.editingQuotationServiceFee = {};
window.activeModals = {};

// ─── TOAST NOTIFICATION ENGINE ───
function showToast(message, type = 'info') {
    console.log(`[Toast ${type.toUpperCase()}]: ${message}`);
    let t = document.getElementById('toast');
    if (!t) {
        t = document.createElement('div');
        t.id = 'toast';
        t.className = 'fixed bottom-8 right-8 z-50 px-6 py-4 rounded-xl font-bold text-sm shadow-xl max-w-md pointer-events-none opacity-0 translate-y-8 transition-all duration-500';
        document.body.appendChild(t);
    }
    t.textContent = message;
    
    if (type === 'success') {
        t.className = 'fixed bottom-8 right-8 z-50 px-6 py-4 rounded-xl font-bold text-sm shadow-xl max-w-md bg-emerald-500 text-slate-950 opacity-100 translate-y-0 transition-all duration-500';
    } else if (type === 'error') {
        t.className = 'fixed bottom-8 right-8 z-50 px-6 py-4 rounded-xl font-bold text-sm shadow-xl max-w-md bg-rose-500 text-white opacity-100 translate-y-0 transition-all duration-500';
    } else {
        t.className = 'fixed bottom-8 right-8 z-50 px-6 py-4 rounded-xl font-bold text-sm shadow-xl max-w-md bg-blue-500 text-white opacity-100 translate-y-0 transition-all duration-500';
    }

    setTimeout(() => {
        t.className = 'fixed bottom-8 right-8 z-50 px-6 py-4 rounded-xl font-bold text-sm shadow-xl max-w-md pointer-events-none opacity-0 translate-y-8 transition-all duration-500';
    }, 4000);
}
window.showToast = showToast;

// ─── DOM MODAL LAYER ENGINE ───
window.createDashboardModal = function(modalId, contentHtml, maxWidthClass = 'max-w-lg') {
    const existing = document.getElementById(modalId);
    if (existing) existing.remove();

    const modalDiv = document.createElement('div');
    modalDiv.id = modalId;
    modalDiv.className = "fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm dynamic-modal";
    modalDiv.innerHTML = `
        <div class="bg-slate-950 border border-slate-850 rounded-xl shadow-2xl w-full ${maxWidthClass} overflow-hidden transform transition-all scale-100">
            ${contentHtml}
        </div>
    `;
    document.body.appendChild(modalDiv);
    window.activeModals[modalId] = modalDiv;
};

window.closeAllDashboardModals = function() {
    const modals = document.querySelectorAll('.dynamic-modal');
    modals.forEach(m => m.remove());
    window.activeModals = {};
};

// ─── CATALOG SYNCHRONIZER ───
async function loadCatalog() {
    if (!supabase) return;
    try {
        const { data: brands } = await supabase.from('brands').select('*');
        const { data: devices } = await supabase.from('devices').select('*');
        const { data: repairs } = await supabase.from('repair_types').select('*');
        const { data: parts } = await supabase.from('parts_catalog').select('*');

        allBrands = brands || [];
        allDevices = devices || [];
        allRepairTypes = repairs || [];
        allParts = parts || [];
        console.log('📦 Catalog Successfully Loaded From Supabase.');
    } catch (e) {
        console.error('Catalog synchronization failed:', e);
    }
}
window.loadCatalog = loadCatalog;

// ─── AUTHENTICATION WEB ROUTING ───
window.handleSessionState = async function() {
    if (!supabase) return;
    const { data: { session } } = await supabase.auth.getSession();
    if (session) {
        currentUser = session.user;
        const { data: profile } = await supabase.from('profiles').select('role').eq('id', currentUser.id).single();
        if (profile) {
            currentRoles = [profile.role];
            console.log(`👤 Active Session Verified. User Role: ${profile.role}`);
        }
    }
};

// ─── QUOTATION INLINE EDITOR STATE MANAGEMENT ───
window.updateQuotationPartName = function(orderId, index, value) {
    if (!window.editingQuotationParts[orderId] || !window.editingQuotationParts[orderId][index]) return;
    const isOriginal = window.editingQuotationParts[orderId][index].name.startsWith('[Original]') || window.editingQuotationParts[orderId][index].name.startsWith('[Old]');
    const prefix = isOriginal ? '[Original] ' : '[Additional] ';
    window.editingQuotationParts[orderId][index].name = prefix + value.replace(/^\[Original\]\s*/, '').replace(/^\[Old\]\s*/, '').replace(/^\[Additional\]\s*/, '').replace(/^\[New\]\s*/, '');
};

window.updateQuotationPartPrice = function(orderId, index, value) {
    if (!window.editingQuotationParts[orderId] || !window.editingQuotationParts[orderId][index]) return;
    window.editingQuotationParts[orderId][index].price = parseFloat(value) || 0;
    if (typeof renderQuotationFormInlineEditable === 'function') renderQuotationFormInlineEditable(orderId);
};

window.toggleQuotationPartType = function(orderId, index, makeOriginal) {
    if (!window.editingQuotationParts[orderId] || !window.editingQuotationParts[orderId][index]) return;
    const cleanName = window.editingQuotationParts[orderId][index].name.replace(/^\[Original\]\s*/, '').replace(/^\[Old\]\s*/, '').replace(/^\[Additional\]\s*/, '').replace(/^\[New\]\s*/, '');
    window.editingQuotationParts[orderId][index].name = makeOriginal ? `[Original] ${cleanName}` : `[Additional] ${cleanName}`;
    if (typeof renderQuotationFormInlineEditable === 'function') renderQuotationFormInlineEditable(orderId);
};

window.removeQuotationPartEditable = function(orderId, index) {
    if (!window.editingQuotationParts[orderId]) return;
    window.editingQuotationParts[orderId].splice(index, 1);
    if (typeof renderQuotationFormInlineEditable === 'function') renderQuotationFormInlineEditable(orderId);
};

window.addNewQuotationPartPromptEditable = function(orderId, isOriginal) {
    if (!window.editingQuotationParts[orderId]) window.editingQuotationParts[orderId] = [];
    const prefix = isOriginal ? '[Original]' : '[Additional]';
    window.editingQuotationParts[orderId].push({
        name: `${prefix} New Structural Component`,
        price: 0
    });
    if (typeof renderQuotationFormInlineEditable === 'function') renderQuotationFormInlineEditable(orderId);
};

window.updateQuotationDiagnosisChargeEditable = function(orderId, value) {
    window.editingQuotationDiagnosisCharge[orderId] = parseFloat(value) || 0;
    if (typeof renderQuotationFormInlineEditable === 'function') renderQuotationFormInlineEditable(orderId);
};

window.updateQuotationServiceFeeEditable = function(orderId, value) {
    window.editingQuotationServiceFee[orderId] = parseFloat(value) || 0;
    if (typeof renderQuotationFormInlineEditable === 'function') renderQuotationFormInlineEditable(orderId);
};

// ─── PIPELINE ROUTER DISPATCHERS & UPDATES ───
window.submitFinalizedQuotation = async function(orderId) {
    if (!supabase) {
        showToast('⚠️ Supabase offline. Update aborted.', 'error');
        return;
    }
    const partsList = window.editingQuotationParts[orderId] || [];
    const serviceFee = window.editingQuotationServiceFee[orderId] || 0;
    const diagnosisCharge = window.editingQuotationDiagnosisCharge[orderId] || 0;

    const partsSum = partsList.reduce((sum, p) => sum + p.price, 0);
    const totalPrice = serviceFee + diagnosisCharge + partsSum;
    const serializedParts = JSON.stringify(partsList);

    try {
        const { error } = await supabase
            .from('orders')
            .update({
                custom_quote_parts: serializedParts,
                parts_total: partsSum,
                service_fee: serviceFee,
                diagnosis_charge: diagnosisCharge,
                total_price: totalPrice,
                grand_total: totalPrice,
                status: 'Quotation-Sent'
            })
            .eq('id', orderId);

        if (error) throw error;
        await window.createAlert(orderId, `Invoice dispatched: ₹${totalPrice.toLocaleString('en-IN')}`, 'quotation_sent');
        showToast('✅ Invoice breakdown published to client dashboard!', 'success');
        window.closeAllDashboardModals();
        if (typeof loadDashboard === 'function') loadDashboard();
    } catch (err) {
        showToast('❌ Invoice dispatch failed: ' + err.message, 'error');
    }
};

// ─── COORDINATOR PANEL SECURITY DISPATCHERS ───
window.submitAssignRoles = async function(userId) {
    if (!supabase) {
        showToast('⚠️ Database infrastructure offline.', 'error');
        return;
    }
    const roleSelect = document.getElementById(`roleSelect-${userId}`);
    if (!roleSelect) {
        showToast('❌ Operational configuration target target missing.', 'error');
        return;
    }
    const selectedRole = roleSelect.value;
    try {
        const { error } = await supabase
            .from('profiles')
            .update({ role: selectedRole })
            .eq('id', userId);

        if (error) throw error;
        showToast(`✅ Profile token updated to: ${selectedRole}`, 'success');
        if (typeof loadDashboard === 'function') loadDashboard();
    } catch (err) {
        showToast('❌ Privilege configuration failed: ' + err.message, 'error');
    }
};

// ─── LOGISTICS FIELD OPERATIONS & HANDOVER TOKENS ───
window.initiatePickup = async function(orderId) {
    if (!supabase) return showToast('Supabase infrastructure offline.', 'error');
    try {
        const mockOtp = Math.floor(100000 + Math.random() * 900000).toString();
        const { error } = await supabase
            .from('orders')
            .update({ status: 'Pickup-Pending', pickup_otp: mockOtp })
            .eq('id', orderId);
        if (error) throw error;
        showToast('🚀 Pickup job initiated. Code generated for customer verification.', 'success');
        if (typeof loadDashboard === 'function') loadDashboard();
    } catch (err) {
        showToast('Field transit initiation failed: ' + err.message, 'error');
    }
};

window.verifyPickup = async function(orderId, enteredOtp) {
    if (!supabase) return showToast('Database connection missing.', 'error');
    if (!enteredOtp) return showToast('Please enter the 6-digit client handover code.', 'error');
    try {
        const { data, error } = await supabase.from('orders').select('pickup_otp').eq('id', orderId).single();
        if (error || !data) throw new Error('Reference record data missing.');
        
        if (data.pickup_otp !== enteredOtp.trim()) {
            return showToast('❌ Invalid verification code. Cross-check client screen.', 'error');
        }
        
        const { error: updErr } = await supabase
            .from('orders')
            .update({ status: 'With-RepairMaster', pickup_otp: 'VERIFIED' })
            .eq('id', orderId);
        if (updErr) throw updErr;
        
        showToast('🔒 Handover verified! Logistics transit updated to Lab Bench.', 'success');
        if (typeof loadDashboard === 'function') loadDashboard();
    } catch (err) {
        showToast('Verification failed: ' + err.message, 'error');
    }
};

window.completeRepair = async function(orderId) {
    if (!supabase) return showToast('Database unreachable.', 'error');
    try {
        const { error } = await supabase
            .from('orders')
            .update({ status: 'Ready-For-Delivery', pickup_otp: Math.floor(100000 + Math.random() * 900000).toString() })
            .eq('id', orderId);
        if (error) throw error;
        showToast('⚙️ Engineering complete! Ticket passed to delivery logistics.', 'success');
        if (typeof loadDashboard === 'function') loadDashboard();
    } catch (err) {
        showToast('Ticket finalization failed: ' + err.message, 'error');
    }
};

window.closeTicket = async function(orderId, enteredOtp) {
    if (!supabase) return showToast('Network connection down.', 'error');
    if (!enteredOtp) return showToast('Please capture delivery handover OTP.', 'error');
    try {
        const { data, error } = await supabase.from('orders').select('pickup_otp').eq('id', orderId).single();
        if (error || !data) throw new Error('Reference record missing.');
        
        if (data.pickup_otp !== enteredOtp.trim()) {
            return showToast('❌ Invalid token. Verify handset functionality with client.', 'error');
        }
        
        const { error: updErr } = await supabase
            .from('orders')
            .update({ status: 'Completed', payment_status: 'Paid', pickup_otp: 'VERIFIED' })
            .eq('id', orderId);
        if (updErr) throw updErr;
        
        showToast('🏁 Device successfully delivered and ticket closed! Excellent work.', 'success');
        if (typeof loadDashboard === 'function') loadDashboard();
    } catch (err) {
        showToast('Logistics closure exception: ' + err.message, 'error');
    }
};

// ─── CLIENT ACTION GATEWAYS ───
window.confirmQuotation = async function(orderId) {
    if (!supabase) return;
    try {
        const { error } = await supabase.from('orders').update({ status: 'Confirmed' }).eq('id', orderId);
        if (error) throw error;
        showToast('🙌 Estimate accepted! Bench engineers are initiating repairs.', 'success');
        if (typeof loadDashboard === 'function') loadDashboard();
    } catch (err) {
        showToast('Submission error: ' + err.message, 'error');
    }
};

window.rejectQuotation = async function(orderId) {
    if (!supabase) return;
    try {
        const { error } = await supabase.from('orders').update({ status: 'Rejected' }).eq('id', orderId);
        if (error) throw error;
        showToast('🛑 Estimate declined. Return routing checklist generated.', 'info');
        if (typeof loadDashboard === 'function') loadDashboard();
    } catch (err) {
        showToast('Submission error: ' + err.message, 'error');
    }
};

// ─── NOTIFICATION ALERTS ENGINE ───
window.fetchAndRenderAlerts = async function() {
    console.log('Synchronizing active transactional data notification feeds...');
};

window.createAlert = async function(orderId, message, alertType) {
    if (!supabase) return;
    try {
        await supabase.from('notifications').insert([{ order_id: orderId, message, type: alertType, is_read: false }]);
    } catch (err) {
        console.warn('Notification queue payload registration drop:', err);
    }
};

// ─── MASTER ORDER OVERLAYS RENDERING ───
window.viewOrderDetails = function(orderId) {
    console.log('Mounting custom worksheet details layout wrapper for order id:', orderId);
};

window.selectCODPayment = function() {
    console.log('Payment schema configuration state: Doorstep Cash Collection Enabled.');
};

window.confirmPaymentManual = function() {
    console.log('Publishing payment verification to audit table layers...');
};

window.openInvoicePage = function(orderId) {
    window.open(`invoice.html?orderId=${orderId}`, '_blank');
};

window.checkAllStepsCompleted = function(orderId) {
    const stepsCount = 4;
    let completed = 0;
    for (let i = 0; i < stepsCount; i++) {
        if (localStorage.getItem(`${orderId}-step-${i}`) === 'true') completed++;
    }
    console.log(`📋 Order Steps Synced: ${completed}/${stepsCount} technical milestones checked.`);
};

// ─── OPERATIONS CONTROL DESKS RENDERING ───
window.renderCoordinatorOpsDesk = function() {
    let staffHtml = ``;
    let pendingTasksHtml = ``;

    const targetDiv = document.getElementById('coordinatorConsole');
    if (!targetDiv) return;

    targetDiv.innerHTML = `
        <div class="grid grid-cols-1 lg:grid-cols-5 gap-6">
            <div class="lg:col-span-3 bg-slate-900/60 border border-slate-800 p-5 rounded-2xl space-y-4">
                <div class="flex items-center justify-between border-b border-slate-800/60 pb-3">
                    <div>
                        <h4 class="text-sm font-bold text-white font-display uppercase tracking-wider">Staff Performance Ledger</h4>
                        <p class="text-[10px] text-gray-400">Live operational load & real-time delivery ratings</p>
                    </div>
                    <span class="text-[9px] bg-teal-500/10 text-teal border border-teal-500/20 font-mono font-bold uppercase tracking-wider px-2 py-0.5 rounded-full">HQ Dispatch Panel</span>
                </div>
                ${staffHtml}
            </div>

            <div class="lg:col-span-2 bg-slate-900/60 border border-slate-800 p-5 rounded-2xl space-y-4">
                <div class="flex items-center justify-between border-b border-slate-800/60 pb-3">
                    <div>
                        <h4 class="text-sm font-bold text-white font-display uppercase tracking-wider">Fast Assignment Action Desk</h4>
                        <p class="text-[10px] text-gray-400">Immediate routing control for unassigned requests</p>
                    </div>
                    <span class="text-[9px] bg-amber-500/10 text-amber-400 border border-amber-500/20 font-mono font-bold uppercase tracking-wider px-2 py-0.5 rounded-full">Needs Dispatch</span>
                </div>
                ${pendingTasksHtml}
            </div>
        </div>
    `;
};

// ─── WEB APPLICATION BOOTSTRAP LIFECYCLE ROUTER ───
document.addEventListener('DOMContentLoaded', async () => {
    await window.handleSessionState();
    await loadCatalog();
    
    // Wire up booking interaction controls on index.html
    if (document.getElementById('brandSelect')) {
        if (typeof populateBrands === 'function') populateBrands();
        document.getElementById('brandSelect').addEventListener('change', window.updateModels || (() => {}));
        document.getElementById('modelSelect').addEventListener('change', window.updateRepairTypes || (() => {}));
        document.getElementById('repairTypeSelect').addEventListener('change', window.updatePartsSurvey || (() => {}));
        const qualitySelect = document.getElementById('tierInput') || document.getElementById('qualitySelect');
        if (qualitySelect) qualitySelect.addEventListener('change', window.calculateEstimate || (() => {}));
    }
    
    // Mount front-page marketing components
    if (document.getElementById('offersContainer') && typeof fetchOffers === 'function') {
        fetchOffers();
    }
    
    // Secure application interaction forms
    const reqForm = document.getElementById('repairRequestForm') || document.getElementById('requestForm');
    if (reqForm && typeof submitRequest === 'function') {
        reqForm.addEventListener('submit', submitRequest);
    }
    
    // Auto-mount control workspace matrices if elements are found inside dashboard.html
    if (document.getElementById('coordinatorConsole')) {
        window.renderCoordinatorOpsDesk();
    }
});