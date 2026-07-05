// ─────────────────────────────────────────────────────────────
//  main.js – Complete Multi-Page DTC RepairMaster Web Controller
// ─────────────────────────────────────────────────────────────

// ─── SUPABASE CREDENTIALS ───
const SUPABASE_URL = 'https://mpcnfrshpgcpmrgledwy.supabase.co';
const SUPABASE_ANON_KEY = 'sb_publishable_IlSzuHbWowZ84IdxRwBCxg_DDT9P_Vz';
const supabase = window.supabase ? window.supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY) : null;

// ─── GLOBAL STATE ───
let allBrands = [];
let allDevices = [];
let allRepairTypes = [];
let allParts = [];
let currentUser = null;
let currentRoles = [];

// ─── TOAST NOTIFICATION ENGINE ───
function showToast(message, type = 'info') {
    console.log(`[Toast ${type.toUpperCase()}]: ${message}`);
    let t = document.getElementById('toast');
    if (!t) {
        // Create toast element on the fly if not exists
        t = document.createElement('div');
        t.id = 'toast';
        t.className = 'fixed bottom-8 right-8 z-50 px-6 py-4 rounded-xl font-bold text-sm shadow-xl max-w-md pointer-events-none opacity-0 translate-y-8 transition-all duration-500';
        document.body.appendChild(t);
    }
    t.textContent = message;
    t.className = `fixed bottom-8 right-8 z-50 px-6 py-4 rounded-xl font-bold text-sm shadow-xl max-w-md pointer-events-auto backdrop-blur-md transition-all duration-500 transform ${
        type === 'success' ? 'bg-teal-600 text-white border border-teal-400 shadow-teal-500/20' :
        type === 'error' ? 'bg-red-600 text-white border border-red-400 shadow-red-500/20' :
        'bg-slate-800 text-white border border-slate-700 shadow-slate-900/40'
    } show`;
    clearTimeout(t._hide);
    t._hide = setTimeout(() => {
        t.className = 'fixed bottom-8 right-8 z-50 px-6 py-4 rounded-xl font-bold text-sm shadow-xl max-w-md pointer-events-none opacity-0 translate-y-8 transition-all duration-500';
    }, 4000);
}

// ─── 1. DATABASE & CATALOG SYNCHRONIZER ───
async function loadCatalog() {
    if (!supabase) {
        useComprehensiveFallback();
        return true;
    }
    try {
        const [brandsRes, devicesRes, repairTypesRes, partsRes] = await Promise.all([
            supabase.from('brands').select('*').order('name'),
            supabase.from('devices').select('*').order('name'),
            supabase.from('repair_types').select('*').order('label'),
            supabase.from('parts').select('*')
        ]);
        if (brandsRes.error) throw brandsRes.error;
        if (devicesRes.error) throw devicesRes.error;
        if (repairTypesRes.error) throw repairTypesRes.error;
        if (partsRes.error) throw partsRes.error;

        allBrands = brandsRes.data || [];
        allDevices = devicesRes.data || [];
        allRepairTypes = repairTypesRes.data || [];
        allParts = partsRes.data || [];

        if (allBrands.length === 0) {
            console.warn('No brands found in Supabase. Loading fallbacks...');
            useComprehensiveFallback();
        } else {
            console.log('✅ Synchronized Catalog with Supabase successfully!');
        }
        return true;
    } catch (err) {
        console.warn('Supabase fetch error. Loading fallbacks...', err);
        useComprehensiveFallback();
        return true;
    }
}

function useComprehensiveFallback() {
    allBrands = [
        { id: 'b1', name: 'Apple' }, { id: 'b2', name: 'Samsung' }, { id: 'b3', name: 'Vivo' },
        { id: 'b4', name: 'OnePlus' }, { id: 'b5', name: 'Xiaomi (MI)' }, { id: 'b6', name: 'Oppo' },
        { id: 'b7', name: 'Realme' }, { id: 'b8', name: 'Google' }, { id: 'b9', name: 'Nothing' },
        { id: 'b10', name: 'Motorola' }, { id: 'b11', name: 'iQOO' }, { id: 'b12', name: 'Lava' }
    ];
    allDevices = [
        { id: 'd1', brand_id: 'b1', name: 'iPhone 15 Pro Max' },
        { id: 'd2', brand_id: 'b1', name: 'iPhone 15' },
        { id: 'd3', brand_id: 'b3', name: 'Vivo V30 Pro' },
        { id: 'd4', brand_id: 'b3', name: 'Vivo V29 Pro' },
        { id: 'd5', brand_id: 'b2', name: 'Galaxy S24 Ultra' },
        { id: 'd6', brand_id: 'b2', name: 'Galaxy A55' },
        { id: 'd7', brand_id: 'b4', name: 'OnePlus 12' },
        { id: 'd8', brand_id: 'b5', name: 'Redmi Note 13 Pro' },
        { id: 'd9', brand_id: 'b6', name: 'Reno 11 Pro' },
        { id: 'd10', brand_id: 'b7', name: 'GT 6 Pro' },
        { id: 'd11', brand_id: 'b8', name: 'Pixel 8 Pro' },
        { id: 'd12', brand_id: 'b9', name: 'Phone 2' },
        { id: 'd13', brand_id: 'b10', name: 'Edge 50 Pro' },
        { id: 'd14', brand_id: 'b11', name: 'iQOO 12' },
        { id: 'd15', brand_id: 'b12', name: 'Agni 2' }
    ];
    allRepairTypes = [
        { id: 'rt1', name: 'screen', label: '📱 Screen Replacement' },
        { id: 'rt2', name: 'battery', label: '🔋 Battery Replacement' },
        { id: 'rt3', name: 'chargingport', label: '🔌 Charging Port Repair' },
        { id: 'rt4', name: 'camera', label: '📷 Camera Repair' },
        { id: 'rt5', name: 'speaker', label: '🔊 Speaker / Mic Repair' },
        { id: 'rt6', name: 'button', label: '🔘 Button Repair' },
        { id: 'rt7', name: 'motherboard', label: '💻 Motherboard Repair' },
        { id: 'rt8', name: 'waterdamage', label: '💧 Water Damage Repair' },
        { id: 'rt9', name: 'software', label: '📀 Software / OS Repair' },
        { id: 'rt10', name: 'network', label: '📶 Network / Antenna Repair' },
        { id: 'rt11', name: 'completeoverhaul', label: '⚙️ Complete Overhaul' }
    ];
    allParts = [
        { device_id: 'd3', repair_type_id: 'rt1', name: 'AMOLED Screen Panel Assembly', price: 6300 },
        { device_id: 'd3', repair_type_id: 'rt1', name: 'Digitizer & Display Flex', price: 504 },
        { device_id: 'd3', repair_type_id: 'rt1', name: 'Water-Resistant Frame Adhesive Seal', price: 126 },
        { device_id: 'd3', repair_type_id: 'rt2', name: 'Certified Li-Po 5000mAh Battery Cell', price: 1500 },
        { device_id: 'd3', repair_type_id: 'rt2', name: 'Thermal Dissipation Pad', price: 150 },
        
        { device_id: 'd1', repair_type_id: 'rt1', name: 'Super Retina XDR OLED Display', price: 25200 },
        { device_id: 'd1', repair_type_id: 'rt1', name: 'Force Touch Digitizer Sensor', price: 2016 },
        { device_id: 'd1', repair_type_id: 'rt1', name: 'IP68 Watertight Perimeter Seal Adhesive', price: 504 },
        { device_id: 'd1', repair_type_id: 'rt2', name: 'OEM Battery Cell Replacement', price: 4500 },
        
        { device_id: 'd5', repair_type_id: 'rt1', name: 'Dynamic AMOLED 2X Display Module', price: 21500 },
        { device_id: 'd5', repair_type_id: 'rt1', name: 'Corning Gorilla Armor Glass Layer', price: 3200 },
        { device_id: 'd5', repair_type_id: 'rt2', name: 'Official Li-Ion 5000mAh Battery Pack', price: 2800 },
        { device_id: 'd5', repair_type_id: 'rt3', name: 'USB-C SuperFast Charging Port PCB', price: 1400 },
        { device_id: 'd5', repair_type_id: 'rt7', name: 'Logic Board IC Power Management Chip', price: 5500 }
    ];

    // Generate remaining missing configurations dynamically
    allDevices.forEach(d => {
        allRepairTypes.forEach(rt => {
            const exists = allParts.some(p => String(p.device_id) === String(d.id) && String(p.repair_type_id) === String(rt.id));
            if (!exists) {
                let baseP = 1500;
                if (rt.name === 'screen') baseP = 4200;
                else if (rt.name === 'battery') baseP = 1600;
                else if (rt.name === 'motherboard') baseP = 5000;
                else if (rt.name === 'chargingport') baseP = 1000;

                if (d.brand_id === 'b1') baseP *= 1.5;
                else if (d.brand_id === 'b2') baseP *= 1.2;

                allParts.push({
                    device_id: d.id,
                    repair_type_id: rt.id,
                    name: `Premium ${rt.name.toUpperCase()} Replacement Kit`,
                    price: Math.round(baseP)
                });
            }
        });
    });

    console.log('📦 Loaded fallbacks with generated coverage.');
}

function getDeviceName(deviceId) {
    if (!deviceId) return 'Generic Device';
    const dev = allDevices.find(d => String(d.id) === String(deviceId));
    return dev ? dev.name : 'Device';
}
window.getDeviceName = getDeviceName;

function getRepairLabel(repairTypeId) {
    if (!repairTypeId) return 'Device Repair';
    const rt = allRepairTypes.find(r => String(r.id) === String(repairTypeId));
    return rt ? rt.label : 'Repair';
}
window.getRepairLabel = getRepairLabel;

function buildSingleOrderCardHtml(o, isAdmin, isCoordinator, isTechnician, isRepairMaster, isGuestMode = false, isMatched = true) {
    const status = o.status || 'Pending';
    const statusClass = 'status-' + status.replace(/\s/g, '-');
    const deviceName = getDeviceName(o.device_id) !== 'Device' ? getDeviceName(o.device_id) : (o.device_other || 'Device');
    const repairLabel = getRepairLabel(o.repair_type_id) !== 'Repair' ? getRepairLabel(o.repair_type_id) : (o.repair_other || 'Repair');

    let actions = '';
    if (!isGuestMode) {
        if (isAdmin || isCoordinator) {
            if (status === 'Pending') {
                actions += `
                    <button onclick="assignOrderRoles('${o.id}')" class="action-btn btn-assign">Assign Staff</button>
                `;
            }
            if (isCoordinator) {
                if (status === 'Pending' || status === 'Technician Assigned' || status === 'RepairMaster Assigned') {
                    actions += `
                        <button onclick="assignSelfAsTechnician('${o.id}')" class="action-btn btn-pickup">Take as Tech</button>
                        <button onclick="assignSelfAsRepairMaster('${o.id}')" class="action-btn btn-diagnose">Take as Master</button>
                    `;
                }
            }
            if (['Technician Assigned', 'RepairMaster Assigned', 'Pickup-Pending', 'With-RepairMaster'].includes(status)) {
                actions += `
                    <button onclick="sendQuotation('${o.id}')" class="action-btn btn-quote">Manage Price</button>
                `;
            }
        }

        if (isTechnician && o.technician_id === currentUser?.id) {
            if (status === 'Technician Assigned') {
                actions += `<button onclick="initiatePickup('${o.id}')" class="action-btn btn-pickup">Start Pickup</button>`;
            } else if (status === 'Pickup-Pending') {
                actions += `
                    <div class="flex items-center gap-1 mt-1">
                        <input type="text" id="otp-${o.id}" placeholder="Pickup OTP" class="otp-input p-1.5 rounded-lg text-xs w-24 border border-teal-500/20 bg-slate-900 text-white mr-2"/>
                        <button onclick="verifyPickup('${o.id}', document.getElementById('otp-${o.id}').value)" class="action-btn btn-verify">Verify Handover</button>
                    </div>
                `;
            } else if (status === 'Ready-For-Delivery') {
                actions += `
                    <div class="flex items-center gap-1 mt-1">
                        <input type="text" id="delivery-otp-${o.id}" placeholder="Handover OTP" class="otp-input p-1.5 rounded-lg text-xs w-24 border border-teal-500/20 bg-slate-900 text-white mr-2"/>
                        <button onclick="closeTicket('${o.id}', document.getElementById('delivery-otp-${o.id}').value)" class="action-btn btn-verify">Verify Delivery</button>
                    </div>
                `;
            }
        }

        if (isRepairMaster && o.repairmaster_id === currentUser?.id) {
            if (status === 'With-RepairMaster') {
                actions += `
                   <button onclick="updateDiagnosis('${o.id}')" class="action-btn btn-diagnose">Diagnose Logs</button>
<button onclick="requestAdditionalParts('${o.id}')" class="action-btn btn-part">+ Add Part</button>
                `;
            } else if (status === 'Confirmed') {
                actions += `
                    <div class="flex flex-col gap-1 items-end">
                        <span class="text-xs text-emerald-400 font-bold"><i class="fa-solid fa-spinner fa-spin mr-1"></i> Under Active Work</span>
                        <button onclick="completeRepair('${o.id}')" class="action-btn btn-confirm py-1 px-3 mt-1 text-[11px]">Finish Repair</button>
                    </div>
                `;
            }
        }
    }

    // Customer & Guest Actions
    const isClient = isGuestMode || (currentUser && o.user_id === currentUser.id && !isAdmin && !isCoordinator && !isTechnician && !isRepairMaster);
    if (isClient) {
        if (status === 'Quotation-Sent') {
            actions += `
                <button onclick="confirmQuotation('${o.id}')" class="action-btn btn-confirm">Accept Quote</button>
                <button onclick="rejectQuotation('${o.id}')" class="action-btn btn-reject">Decline</button>
            `;
        } else if (status === 'Awaiting-Payment' || (status === 'Rejected' && (o.total_price || 0) > 0)) {
            const labelPay = status === 'Rejected' ? `Pay Rejection Fee (₹${(o.total_price || 0).toLocaleString('en-IN')})` : `💳 Pay ₹${(o.total_price || 0).toLocaleString('en-IN')}`;
            actions += `
                <button onclick="payForRepair('${o.id}', ${o.total_price || 0}, '${deviceName.replace(/'/g, "\\'")}')" class="action-btn btn-confirm">${labelPay}</button>
            `;
        } else if (status === 'Completed' || o.pickup_otp === 'VERIFIED') {
            // If they have not left a rating yet, allow writing a review!
            if (!o.customer_rating) {
                actions += `
                    <div class="mt-4 p-4 rounded-xl bg-slate-900 border border-slate-800 text-left max-w-sm w-full">
                        <p class="text-xs font-bold text-white mb-2"><i class="fa-regular fa-star text-tealAccent mr-1"></i> Rate Your Doorstep Experience</p>
                        <div class="flex gap-1 mb-2">
                            ${[1, 2, 3, 4, 5].map(star => `
                                <button onclick="this.parentElement.setAttribute('data-rating', '${star}'); Array.from(this.parentElement.children).forEach((el, idx) => el.className = idx < ${star} ? 'text-amber-400' : 'text-gray-600')" class="text-gray-600 text-lg transition"><i class="fa-solid fa-star"></i></button>
                            `).join('')}
                        </div>
                        <textarea id="review-text-${o.id}" placeholder="Any suggestions or feedback? (e.g. Excellent doorstep technician support in Wardha!)" class="w-full bg-slate-950 border border-slate-800 p-2 rounded-lg text-xs text-white outline-none mb-2" rows="2"></textarea>
                        <button onclick="const starVal = this.parentElement.querySelector('[data-rating]')?.getAttribute('data-rating') || '5'; submitOrderReview('${o.id}', parseInt(starVal), document.getElementById('review-text-${o.id}').value)" class="bg-teal px-3 py-1.5 rounded-lg text-slate-950 hover:bg-teal-500 font-bold text-[10px] transition">Submit Review</button>
                    </div>
                `;
            } else {
                actions += `
                    <div class="mt-2 text-xs text-amber-400 font-medium">
                        <span>Your Rating: ${'⭐'.repeat(o.customer_rating)}</span>
                        ${o.customer_review ? `<p class="text-gray-400 italic mt-1">"${o.customer_review}"</p>` : ''}
                    </div>
                `;
            }
        }
    }

    let otpNoticeHtml = '';
    if (isClient) {
        if (status === 'Pickup-Pending' && o.pickup_otp) {
            otpNoticeHtml = `
                <div class="mt-3 p-3 rounded-lg bg-teal-500/10 border border-teal-500/20 text-xs text-teal-300 flex items-center justify-between">
                    <span>🔑 Pickup Handover Code: <span class="text-gray-400 italic">(Show to Technician)</span></span>
                    <strong class="text-sm text-white tracking-widest bg-teal-900/60 px-3 py-1 rounded border border-teal-500/30">${o.pickup_otp}</strong>
                </div>
            `;
        } else if (status === 'Ready-For-Delivery' && o.pickup_otp && o.pickup_otp !== 'VERIFIED') {
            otpNoticeHtml = `
                <div class="mt-3 p-3 rounded-lg bg-teal-500/10 border border-teal-500/20 text-xs text-teal-300 flex items-center justify-between">
                    <span>🔑 Delivery Handover Code: <span class="text-gray-400 italic">(Share with Technician)</span></span>
                    <strong class="text-sm text-white tracking-widest bg-teal-900/60 px-3 py-1 rounded border border-teal-500/30">${o.pickup_otp}</strong>
                </div>
            `;
        }
    }

    let quotationHtml = '';
    if (status === 'Quotation-Sent' || o.total_price) {
        quotationHtml = `
            <div class="quotation-box mt-3 text-xs text-amberAccent bg-amberAccent/5 border border-amberAccent/20 p-3 rounded-lg">
                <p class="font-bold uppercase tracking-wider mb-1"><i class="fa-solid fa-receipt mr-1"></i> Finalized Quotation Summary</p>
                <p class="text-grayText">Estimation finalized: <strong>₹${(o.total_price || 0).toLocaleString('en-IN')}</strong> ${status === 'Quotation-Sent' ? '<span class="text-amber-400">(Awaiting your acceptance)</span>' : ''}</p>
                <div class="flex gap-4 text-[10px] text-gray-400 mt-1">
                    <span>🩺 Diagnosis: ₹${o.diagnosis_charge || 250}</span>
                    <span>🔧 Service/Workmanship: ₹${o.service_fee || 100}</span>
                    <span>🛠️ Parts: ₹${o.parts_total || 0}</span>
                </div>
            </div>
        `;
    }

    // Live Workflow Tracking Indicator
    let workflowHtml = '';
    if (isClient) {
        const steps = [
            { name: 'Placed', active: true },
            { name: 'Assigned', active: ['Technician Assigned', 'Pickup-Pending', 'With-RepairMaster', 'Quotation-Sent', 'Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
            { name: 'Pickup', active: ['Pickup-Pending', 'With-RepairMaster', 'Quotation-Sent', 'Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
            { name: 'Lab Diagnosed', active: ['With-RepairMaster', 'Quotation-Sent', 'Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
            { name: 'Quoted', active: ['Quotation-Sent', 'Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
            { name: 'Repairing', active: ['Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
            { name: 'Paid', active: ['Ready-For-Delivery', 'Completed'].includes(status) },
            { name: 'Delivered', active: ['Completed'].includes(status) }
        ];
        workflowHtml = `
            <div class="mt-4 border-t border-grayBorder pt-3">
                <p class="text-[10px] text-grayText font-bold uppercase tracking-widest mb-2"><i class="fa-solid fa-route mr-1 text-tealAccent"></i> Live Tracking Workflow</p>
                <div class="flex items-center justify-between text-[10px] text-center gap-1">
                    ${steps.map(s => `
                        <div class="flex-1">
                            <div class="h-1.5 w-full rounded-full mb-1 ${s.active ? 'bg-teal-500 shadow-sm shadow-teal-500/20' : 'bg-slate-800'}"></div>
                            <span class="${s.active ? 'text-teal-400 font-bold' : 'text-gray-600'} text-[9px] block leading-tight">${s.name}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    // Role-Based Customer Metadata Panel (Access Control - Protect Customer Data)
    let metadataPanel = '';
    if (!isClient) {
        if (isAdmin || isCoordinator || isTechnician) {
            metadataPanel = `
                <div class="mt-3 p-3 bg-slate-900/80 border border-slate-800 rounded-xl text-xs text-gray-300">
                    <p class="font-bold text-white mb-1 uppercase tracking-wider text-[10px] flex items-center gap-1 font-display">
                        <i class="fa-regular fa-user-circle text-teal"></i> DTC Customer Contact Details
                    </p>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-x-4 gap-y-1">
                        <div>👤 <strong>Name:</strong> ${o.customer_name || 'N/A'}</div>
                        <div>📞 <strong>Phone:</strong> ${o.customer_phone || 'N/A'}</div>
                        <div>✉️ <strong>Email:</strong> ${o.customer_email || 'N/A'}</div>
                        <div>📍 <strong>Address:</strong> ${o.address || 'N/A'}</div>
                        <div class="md:col-span-2">🎫 <strong>System Reference ID:</strong> ${o.order_number}</div>
                    </div>
                </div>
            `;
        } else if (isRepairMaster) {
            metadataPanel = `
                <div class="mt-3 p-3.5 bg-slate-950/60 border border-amber-500/10 rounded-xl text-xs text-gray-400">
                    <p class="font-bold text-amber-400 mb-1.5 uppercase tracking-wider text-[9px] flex items-center gap-1 font-display">
                        <i class="fa-solid fa-user-shield text-amber-500"></i> Customer Info Masked (Bench Protection)
                    </p>
                    <p class="text-[11px] leading-relaxed text-gray-500">
                        To ensure platform security and client privacy, customer direct identifiers and contact information are masked for Bench RepairMaster roles. Please coordinate logistics or customer approvals with the regional Hub Coordinator.
                    </p>
                </div>
            `;
        }
    }

    const opacityClass = isMatched ? '' : 'opacity-40 hover:opacity-100 transition-opacity duration-300';
    const borderClass = isMatched ? 'border-teal-500/20' : 'border-grayBorder/40';

    return `
        <div class="order-card bg-navyBG/40 border ${borderClass} rounded-xl p-5 hover:border-teal-500/30 transition-all ${opacityClass}">
            <div class="flex flex-wrap items-start justify-between gap-3">
                <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-3 flex-wrap">
                        <span class="text-lg font-bold text-white">${deviceName}</span>
                        <span class="text-sm text-grayText">—</span>
                        <span class="text-sm text-tealAccent font-medium">${repairLabel}</span>
                        <span class="status-badge ${statusClass} text-xs">${status}</span>
                        ${!isMatched ? `<span class="inline-block bg-slate-800 text-gray-400 text-[9px] uppercase font-bold px-2.5 py-0.5 rounded-full">Older Log</span>` : ''}
                    </div>
                    <div class="text-xs text-grayText mt-1">
                        <span>ID: ${o.order_number}</span>
                        <span class="mx-2">•</span>
                        <span>📅 ${new Date(o.created_at).toLocaleDateString()}</span>
                        ${o.address ? `<span class="mx-2">•</span><span>📍 ${o.address}</span>` : ''}
                    </div>
                    ${o.photo_url ? `<img src="${o.photo_url}" class="mt-3 max-h-24 rounded-lg border border-grayBorder" />` : ''}
                    ${o.diagnosis_notes ? `<p class="mt-2 text-xs text-grayText italic bg-navyBG/20 p-2 rounded border border-grayBorder">Lab Diagnosis Logs: ${o.diagnosis_notes}</p>` : ''}
                    ${o.custom_quote_parts ? `<p class="mt-2 text-xs text-amber-300 italic bg-navyBG/20 p-2 rounded border border-grayBorder">Requested Spare Parts: ${o.custom_quote_parts}</p>` : ''}
                    ${metadataPanel}
                    ${quotationHtml}
                    ${otpNoticeHtml}
                    ${workflowHtml}
                </div>
                <div class="flex flex-col items-end gap-2">
                    <span class="text-lg font-black text-tealAccent">₹${(o.total_price || 0).toLocaleString('en-IN')}</span>
                    <div class="flex flex-wrap gap-1 justify-end">${actions}</div>
                </div>
            </div>
        </div>
    `;
}
window.buildSingleOrderCardHtml = buildSingleOrderCardHtml;

// ─── 2. ACTIVE PROMO OFFERS ───
async function fetchOffers() {
    const container = document.getElementById('offersContainer');
    if (!container) return;
    try {
        if (!supabase) throw new Error("No supabase instance");
        const { data, error } = await supabase.from('offers')
            .select('*')
            .eq('is_active', true)
            .order('valid_to', { ascending: false });
        if (error) throw error;
        renderOffers(data || []);
    } catch (err) {
        // Fallback demo offers
        const fallbacks = [
            { id: 1, name: 'Monsoon Screen Guard', description: 'Get a free premium tempered glass screen protector with any display replacement.', discount_percent: 100, valid_to: '2026-08-31', image_url: 'repo-image-folder/device-generic.png' },
            { id: 2, name: 'Independence Battery Deal', description: '15% Off on all smartphone battery replacements. Certified genuine cells only.', discount_percent: 15, valid_to: '2026-08-20', image_url: 'repo-image-folder/technician-device-1.png' },
            { id: 3, name: 'First Time Doorstep Booking', description: 'Flat 50% discount on standard service fee for all new Wardha customers.', discount_percent: 50, valid_to: '2026-12-31', image_url: 'repo-image-folder/technician-scooty.png' }
        ];
        renderOffers(fallbacks);
    }
}

function renderOffers(offers) {
    const container = document.getElementById('offersContainer');
    if (!container) return;
    if (offers.length === 0) {
        container.innerHTML = `<div class="text-center text-grayText/60 py-8 col-span-3">No active promotional offers currently available.</div>`;
        return;
    }
    container.innerHTML = offers.map(o => `
        <div class="offer-card flex flex-col justify-between">
            <div>
                ${o.image_url ? `<img src="${o.image_url}" alt="${o.name}" class="w-full h-40 object-cover rounded-xl mb-4 border border-grayBorder" onerror="this.src='repo-image-folder/device-generic.png'" />` : ''}
                <div class="flex items-start justify-between gap-3 mb-2">
                    <h3 class="text-lg font-bold text-white font-display">${o.name || 'Special Offer'}</h3>
                    <span class="text-2xl font-black text-amberAccent whitespace-nowrap">${o.discount_percent || 0}% OFF</span>
                </div>
                <p class="text-sm text-grayText mb-4">${o.description || ''}</p>
            </div>
            <div>
                <p class="text-xs text-tealAccent mb-4"><i class="fa-solid fa-calendar mr-1"></i> Valid until ${o.valid_to || '31 Dec 2026'}</p>
                <a href="#calculator-section" class="w-full bg-gradient-to-r from-amberAccent to-yellow-500 text-navyBG py-2.5 rounded-xl font-bold hover:scale-[1.02] transition duration-300 block text-center text-sm">Claim Offer</a>
            </div>
        </div>
    `).join('');
}

// ─── 3. ESTIMATION CALCULATOR ENGINE ───
function populateBrands() {
    const select = document.getElementById('brandSelect');
    if (!select) return;
    select.innerHTML = '<option value="">— Select Brand —</option>';
    allBrands.forEach(b => {
        const opt = document.createElement('option');
        opt.value = b.id;
        opt.textContent = b.name;
        if (b.name === "Vivo") opt.selected = true;
        select.appendChild(opt);
    });
    updateModels();
}

function updateModels() {
    const brandId = document.getElementById('brandSelect')?.value;
    const modelSelect = document.getElementById('modelSelect');
    if (!modelSelect) return;
    modelSelect.innerHTML = '<option value="">— Select Model —</option>';

    if (!brandId) return;
    const devices = allDevices.filter(d => String(d.brand_id) === String(brandId));
    devices.forEach((d, i) => {
        const opt = document.createElement('option');
        opt.value = d.id;
        opt.textContent = d.name;
        if (i === 0) opt.selected = true;
        modelSelect.appendChild(opt);
    });
    updateRepairTypes();
}

function updateRepairTypes() {
    const modelId = document.getElementById('modelSelect')?.value;
    const repairSelect = document.getElementById('repairTypeSelect');
    if (!repairSelect) return;
    repairSelect.innerHTML = '<option value="">— Select Repair —</option>';

    if (!modelId) return;
    allRepairTypes.forEach((rt, i) => {
        const opt = document.createElement('option');
        opt.value = rt.id;
        opt.textContent = rt.label || rt.name;
        if (rt.name === 'screen') opt.selected = true;
        repairSelect.appendChild(opt);
    });
    updatePartsSurvey();
}

function updatePartsSurvey() {
    const modelId = document.getElementById('modelSelect')?.value;
    const repairTypeId = document.getElementById('repairTypeSelect')?.value;
    const quality = document.getElementById('qualitySelect')?.value || 'standard';
    const offerClaimed = document.getElementById('offerToggle')?.checked || false;
    const surveyContainer = document.getElementById('partsSurveyContainer');

    if (!modelId || !repairTypeId) {
        if (surveyContainer) {
            surveyContainer.innerHTML = `
                <div class="bg-navyBG/30 border border-grayBorder rounded-xl p-4 text-center text-gray-400/50">
                    <i class="fa-solid fa-circle-info mr-2"></i> Select a brand, model, and repair type to see parts.
                </div>
            `;
        }
        return;
    }

    const parts = allParts.filter(p => String(p.device_id) === String(modelId) && String(p.repair_type_id) === String(repairTypeId));
    const qualityMultiplier = quality === 'premium' ? 1.0 : 0.7;

    if (surveyContainer) {
        if (parts.length === 0) {
            surveyContainer.innerHTML = `
                <div class="bg-navyBG/30 border border-grayBorder rounded-xl p-4 text-center text-grayText">
                    No custom components configured for this device. Flat rates will apply.
                </div>
            `;
        } else {
            surveyContainer.innerHTML = `
                <div class="bg-navyBG/30 border border-grayBorder rounded-xl p-4 space-y-2">
                    <p class="text-xs font-bold text-grayText mb-2 uppercase tracking-wider"><i class="fa-solid fa-layer-group mr-1"></i> Part Breakdown</p>
                    ${parts.map(p => `
                        <div class="part-item">
                            <span class="part-name text-xs">${p.name} (${quality.toUpperCase()})</span>
                            <span class="part-price text-xs">₹${(p.price * qualityMultiplier).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
                        </div>
                    `).join('')}
                </div>
            `;
        }
    }

    let partsTotal = parts.reduce((sum, p) => sum + (p.price * qualityMultiplier), 0);
    const discountedParts = partsTotal * 0.9; // Standard 10% discount on spares
    let serviceFee = discountedParts * 0.15; // standard 15% service charge
    if (offerClaimed) {
        serviceFee = serviceFee * 0.5; // Claim 50% discount on service
    }
    const diagnosisCharge = 250.0;
    const totalEstimate = discountedParts + serviceFee + diagnosisCharge;

    const partsTotalDisplay = document.getElementById('partsTotalDisplay');
    const serviceFeeDisplay = document.getElementById('serviceFeeDisplay');
    const diagnosisChargeDisplay = document.getElementById('diagnosisChargeDisplay');
    const totalPriceDisplay = document.getElementById('totalPriceDisplay');
    const btn = document.getElementById('bookServiceBtn');

    if (partsTotalDisplay) partsTotalDisplay.innerHTML = '₹' + discountedParts.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    if (serviceFeeDisplay) serviceFeeDisplay.innerHTML = '₹' + serviceFee.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    if (diagnosisChargeDisplay) diagnosisChargeDisplay.innerHTML = '₹' + diagnosisCharge.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    if (totalPriceDisplay) totalPriceDisplay.innerHTML = '₹' + totalEstimate.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

    if (btn) {
        if (totalEstimate > 15000) {
            btn.className = 'flex-1 text-center bg-amberAccent text-navyBG font-extrabold py-3.5 rounded-xl shadow-md hover:scale-[1.02] transition-all';
            btn.innerHTML = '<i class="fa-regular fa-message mr-2"></i> Request Lab Quotation';
        } else {
            btn.className = 'flex-1 text-center btn-teal font-extrabold py-3.5 rounded-xl shadow-md hover:scale-[1.02] transition-all';
            btn.innerHTML = '<i class="fa-regular fa-calendar-check mr-2"></i> Book Doorstep Service';
        }
    }
}

// ─── 4. AUTHENTICATED USER ROLES ───
async function getUserRoles(userId) {
    if (!userId || !supabase) return ['customer'];
    try {
        const { data, error } = await supabase
            .from('user_roles')
            .select('roles(name)')
            .eq('user_id', userId);
        if (error || !data) return ['customer'];
        return data.map(row => row.roles?.name).filter(Boolean);
    } catch {
        return ['customer'];
    }
}

async function getCoordinatorId() {
    if (!supabase) return null;
    try {
        const { data, error } = await supabase
            .from('user_roles')
            .select('user_id')
            .eq('role_id', 2)
            .limit(1);
        if (error || !data || data.length === 0) return null;
        return data[0].user_id;
    } catch {
        return null;
    }
}

// ─── 5. SUBMIT REQUEST PIPELINE ───
async function submitRequest(e) {
    e.preventDefault();
    if (!supabase) {
        showToast('⚠️ Supabase connection is offline.', 'error');
        return;
    }
    const name = document.getElementById('reqName').value.trim();
    const phone = document.getElementById('reqPhone').value.trim();
    const email = document.getElementById('reqEmail').value.trim();
    const brandSelect = document.getElementById('reqBrand');
    const modelSelect = document.getElementById('reqModel');
    const repairSelect = document.getElementById('reqRepairType');
    const addressLine = document.getElementById('reqAddressLine').value.trim();
    const city = document.getElementById('reqCity').value;
    const notes = document.getElementById('reqNotes')?.value.trim() || '';

    if (!city || city === 'Nagpur' || city === 'Amravati') {
        showToast('We currently only serve Wardha. Nagpur & Amravati coming soon!', 'error');
        return;
    }

    let deviceId = brandSelect.value;
    let modelId = modelSelect.value;
    let repairTypeId = repairSelect.value;
    let deviceOther = null;
    let repairOther = null;

    if (document.getElementById('reqBrandOther')?.classList.contains('visible')) {
        deviceOther = document.getElementById('reqBrandOtherInput').value.trim();
        if (!deviceOther) return showToast('Please enter the brand name.', 'error');
        deviceId = null;
    }
    if (document.getElementById('reqModelOther')?.classList.contains('visible')) {
        const otherModel = document.getElementById('reqModelOtherInput').value.trim();
        if (!otherModel) return showToast('Please enter the model name.', 'error');
        deviceOther = deviceOther ? deviceOther + ' - ' + otherModel : otherModel;
        modelId = null;
    }
    if (document.getElementById('reqRepairOther')?.classList.contains('visible')) {
        repairOther = document.getElementById('reqRepairOtherInput').value.trim();
        if (!repairOther) return showToast('Please enter the repair type.', 'error');
        repairTypeId = null;
    }

    if (!deviceId && !deviceOther) return showToast('Please select or enter a brand.', 'error');
    if (!modelId && !deviceOther) return showToast('Please select or enter a model.', 'error');
    if (!repairTypeId && !repairOther) return showToast('Please select or enter a repair type.', 'error');

    const photoFile = document.getElementById('reqPhoto')?.files[0];
    let photoUrl = null;
    if (photoFile) {
        const fileExt = photoFile.name.split('.').pop();
        const fileName = `${Date.now()}.${fileExt}`;
        const filePath = `requests/${fileName}`;
        const { error: uploadError } = await supabase.storage
            .from('RequestBucket')
            .upload(filePath, photoFile);
        if (uploadError) {
            showToast('Photo upload failed: ' + uploadError.message, 'error');
            return;
        }
        const { data: urlData } = supabase.storage
            .from('RequestBucket')
            .getPublicUrl(filePath);
        photoUrl = urlData.publicUrl;
    }

    const session = await supabase.auth.getSession();
    const user = session.data?.session?.user || null;

    let partsTotal = 0;
    if (modelId && repairTypeId) {
        const parts = allParts.filter(p => String(p.device_id) === String(modelId) && String(p.repair_type_id) === String(repairTypeId));
        partsTotal = parts.reduce((sum, p) => sum + (p.price * 0.7), 0);
    }
    const discountedParts = partsTotal * 0.9;
    const serviceFee = discountedParts > 0 ? (discountedParts * 0.15) : 100.00;
    const totalEstimate = discountedParts + serviceFee + 250;

    const orderData = {
        order_number: 'RM-REQ-' + Date.now().toString(36).toUpperCase(),
        user_id: user?.id || null,
        customer_name: name,
        customer_phone: phone,
        customer_email: email,
        device_id: modelId || null,
        repair_type_id: repairTypeId || null,
        device_other: deviceOther || null,
        repair_other: repairOther || null,
        photo_url: photoUrl,
        address: addressLine + ', ' + city,
        parts_quality: 'standard',
        parts_total: discountedParts,
        service_fee: serviceFee,
        diagnosis_charge: 250,
        total_price: totalEstimate,
        discount_applied: 0,
        status: 'Pending',
        notes: notes || null,
        created_at: new Date().toISOString()
    };

    try {
        const { data, error } = await supabase.from('orders').insert([orderData]).select();
        if (error) throw error;
        const coordinatorId = await getCoordinatorId();
        if (coordinatorId && data[0]) {
            await supabase.from('orders').update({ assigned_to: coordinatorId }).eq('id', data[0].id);
        }
        const orderNumber = data[0]?.order_number || orderData.order_number;
        const successDiv = document.getElementById('requestSuccess');
        if (successDiv) {
            successDiv.classList.remove('hidden');
            successDiv.innerHTML = `
                <i class="fa-regular fa-circle-check mr-2"></i>
                Request submitted successfully! Reference: <strong>#${orderNumber}</strong>. Our service coordinators will assign a technician to Wardha shortly.
            `;
        }
        e.target.reset();
        showToast('✅ Service request submitted!', 'success');
        if (user) setTimeout(() => { window.location.href = 'dashboard.html'; }, 2000);
    } catch (err) {
        showToast('❌ Failed to submit: ' + err.message, 'error');
    }
}

// ─── 6. CREATE INSTANT ORDER (FROM WEB CALC) ───
async function createOrder() {
    if (!supabase) {
        showToast('⚠️ Supabase connection is offline.', 'error');
        return;
    }
    const brandId = document.getElementById('brandSelect').value;
    const deviceId = document.getElementById('modelSelect').value;
    const repairTypeId = document.getElementById('repairTypeSelect').value;
    const quality = document.getElementById('qualitySelect').value;
    const offerClaimed = document.getElementById('offerToggle').checked;
    if (!brandId || !deviceId || !repairTypeId) {
        showToast('⚠️ Please select brand, model, and repair type first.', 'error');
        return;
    }
    const session = await supabase.auth.getSession();
    const user = session.data?.session?.user || null;
    if (!user) {
        showToast('🔑 Please sign in or create an account to book your doorstep repair.', 'error');
        setTimeout(() => { window.location.href = 'login.html'; }, 1500);
        return;
    }

    const parts = allParts.filter(p => String(p.device_id) === String(deviceId) && String(p.repair_type_id) === String(repairTypeId));
    const qualityMultiplier = quality === 'premium' ? 1.0 : 0.7;
    const partsTotal = parts.reduce((sum, p) => sum + (p.price * qualityMultiplier), 0);
    const discountedParts = partsTotal * 0.9;
    let serviceFee = discountedParts * 0.15;
    if (offerClaimed) serviceFee = serviceFee * 0.5;
    const total = discountedParts + serviceFee + 250;

    const orderData = {
        order_number: 'RM-BOOK-' + Date.now().toString(36).toUpperCase(),
        user_id: user.id,
        customer_name: user.user_metadata?.full_name || 'Web Member',
        customer_phone: user.user_metadata?.phone || '9999999999',
        customer_email: user.email,
        device_id: deviceId,
        repair_type_id: repairTypeId,
        parts_quality: quality,
        parts_total: discountedParts,
        service_fee: serviceFee,
        diagnosis_charge: 250,
        total_price: total,
        discount_applied: offerClaimed ? 0.5 : 0,
        status: 'Pending',
        notes: 'Instantly booked via online estimator breakdown'
    };

    try {
        const { data, error } = await supabase.from('orders').insert([orderData]).select();
        if (error) throw error;
        const coordinatorId = await getCoordinatorId();
        if (coordinatorId && data[0]) {
            await supabase.from('orders').update({ assigned_to: coordinatorId }).eq('id', data[0].id);
        }
        showToast('🎉 Order created successfully! Track via dashboard.', 'success');
        setTimeout(() => { window.location.href = 'dashboard.html'; }, 1500);
    } catch (err) {
        showToast('❌ Order booking failed: ' + err.message, 'error');
    }
}

// ─── 7. MULTI-ROLE TRANSITIONS & CUSTOM QUOTATION FLOW ───
async function assignOrderRoles(orderId) {
    if (!supabase) return showToast('Supabase not connected', 'error');
    try {
        // Fetch user_roles for technicians (3) and repairmasters (4)
        const { data: userRoles, error: roleError } = await supabase
            .from('user_roles')
            .select('user_id, role_id')
            .in('role_id', [3, 4]);

        if (roleError) throw roleError;

        if (!userRoles || userRoles.length === 0) {
            showToast('No technicians or repairmasters found in the system.', 'error');
            return;
        }

        const userIds = userRoles.map(r => r.user_id);

        // Fetch user profiles (names & emails)
        const { data: userProfiles, error: profileError } = await supabase
            .from('users')
            .select('id, name, email')
            .in('id', userIds);

        if (profileError) throw profileError;

        // Merge data
        const usersWithDetails = userRoles.map(ur => {
            const profile = userProfiles.find(p => p.id === ur.user_id);
            const displayName = profile?.name || profile?.email || 'Unknown User';
            return {
                user_id: ur.user_id,
                role_id: ur.role_id,
                displayName: displayName,
                roleName: ur.role_id === 3 ? 'repairmaster' : 'technician'
            };
        });

        // Build modal with dropdowns
        const modal = document.createElement('div');
        modal.id = 'assignModal';
        modal.className = 'fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4';
        modal.innerHTML = `
            <div class="bg-slate-900 border border-grayBorder rounded-2xl p-6 max-w-md w-full shadow-2xl">
                <h3 class="text-xl font-bold text-white mb-4">Assign Staff for Order ${orderId}</h3>
                <div class="space-y-4">
                    <div>
                        <label class="text-sm text-grayText">Select Technician</label>
                        <select id="techSelect" class="w-full bg-navyBG border border-grayBorder rounded-lg p-2 text-white">
                            <option value="">— None —</option>
                            ${usersWithDetails.filter(u => u.roleName === 'technician').map(u => 
                                `<option value="${u.user_id}">${u.displayName}</option>`
                            ).join('')}
                        </select>
                    </div>
                    <div>
                        <label class="text-sm text-grayText">Select RepairMaster</label>
                        <select id="masterSelect" class="w-full bg-navyBG border border-grayBorder rounded-lg p-2 text-white">
                            <option value="">— None —</option>
                            ${usersWithDetails.filter(u => u.roleName === 'repairmaster').map(u => 
                                `<option value="${u.user_id}">${u.displayName}</option>`
                            ).join('')}
                        </select>
                    </div>
                    <div class="flex gap-3 pt-2">
                        <button onclick="document.getElementById('assignModal').remove()" class="flex-1 py-2 bg-gray-700 hover:bg-gray-600 rounded-xl text-white">Cancel</button>
                        <button onclick="confirmAssign('${orderId}')" class="flex-1 py-2 bg-teal-600 hover:bg-teal-500 rounded-xl text-white font-bold">Assign</button>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    } catch (err) {
        showToast('Failed to load staff: ' + err.message, 'error');
    }
}

// Global function for the modal button
window.confirmAssign = async function(orderId) {
    const techId = document.getElementById('techSelect').value;
    const masterId = document.getElementById('masterSelect').value;
    if (!techId && !masterId) {
        showToast('Select at least one staff member.', 'error');
        return;
    }
    try {
        const updateData = { status: 'Technician Assigned' };
        if (techId) updateData.technician_id = techId;
        if (masterId) updateData.repairmaster_id = masterId;
        const { error } = await supabase
            .from('orders')
            .update(updateData)
            .eq('id', orderId);
        if (error) throw error;
        showToast('Staff assigned successfully!', 'success');
        document.getElementById('assignModal')?.remove();
        loadDashboard();
    } catch (err) {
        showToast('Assignment failed: ' + err.message, 'error');
    }
};

async function assignSelfAsTechnician(orderId) {
    if (!currentUser || !supabase) return showToast('Authentication required.', 'error');
    try {
        const { error } = await supabase
            .from('orders')
            .update({ technician_id: currentUser.id, status: 'Technician Assigned' })
            .eq('id', orderId);
        if (error) throw error;
        showToast('You are now the active technician for this job.', 'success');
        loadDashboard();
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

async function assignSelfAsRepairMaster(orderId) {
    if (!currentUser || !supabase) return showToast('Authentication required.', 'error');
    try {
        const { error } = await supabase
            .from('orders')
            .update({ repairmaster_id: currentUser.id, status: 'RepairMaster Assigned' })
            .eq('id', orderId);
        if (error) throw error;
        showToast('You are now the active RepairMaster workshop evaluator.', 'success');
        loadDashboard();
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

function generateOTP() {
    return Math.floor(100000 + Math.random() * 900000).toString();
}

async function initiatePickup(orderId) {
    if (!supabase) return;
    const otp = generateOTP();
    try {
        const { error } = await supabase
            .from('orders')
            .update({ pickup_otp: otp, status: 'Pickup-Pending' })
            .eq('id', orderId);
        if (error) throw error;
        // ✅ Removed the alert – technician should not see the OTP
        showToast('🔒 Pickup initiated. Customer will receive the OTP for handover.', 'success');
        loadDashboard();
    } catch (err) {
        showToast('Pickup generation failed: ' + err.message, 'error');
    }
}

async function verifyPickup(orderId, otp) {
    if (!supabase) return;
    try {
        const { data, error } = await supabase
            .from('orders')
            .select('pickup_otp')
            .eq('id', orderId)
            .single();
        if (error) throw error;
        if (data.pickup_otp !== otp) {
            showToast('❌ Invalid validation OTP. Authentication failed.', 'error');
            return;
        }
        await supabase.from('orders').update({ pickup_otp: null, status: 'With-RepairMaster' }).eq('id', orderId);
        showToast('🔒 Verification complete! Device checked in securely.', 'success');
        loadDashboard();
    } catch (err) {
        showToast('Verification failed: ' + err.message, 'error');
    }
}

async function updateDiagnosis(orderId) {
    // Build modal with a textarea for diagnosis notes
    const modal = document.createElement('div');
    modal.id = 'diagnosisModal';
    modal.className = 'fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4';
    modal.innerHTML = `
        <div class="bg-slate-900 border border-grayBorder rounded-2xl p-6 max-w-md w-full shadow-2xl">
            <h3 class="text-xl font-bold text-white mb-4">📋 Lab Diagnosis Logs</h3>
            <div class="space-y-4">
                <div>
                    <label class="text-sm text-grayText">Diagnosis Notes</label>
                    <textarea id="diagnosisNotesInput" rows="4" class="w-full bg-navyBG border border-grayBorder rounded-lg p-2 text-white text-sm focus:border-teal-500 outline-none" placeholder="Enter diagnosis details, observed issues, recommended repairs..."></textarea>
                </div>
                <div class="flex gap-3 pt-2">
                    <button onclick="document.getElementById('diagnosisModal').remove()" class="flex-1 py-2 bg-gray-700 hover:bg-gray-600 rounded-xl text-white">Cancel</button>
                    <button onclick="confirmDiagnosis('${orderId}')" class="flex-1 py-2 bg-teal-600 hover:bg-teal-500 rounded-xl text-white font-bold">Save Logs</button>
                </div>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
}

// Global function for the modal button
window.confirmDiagnosis = async function(orderId) {
    const notes = document.getElementById('diagnosisNotesInput').value.trim();
    if (!notes) {
        showToast('Please enter diagnosis notes.', 'error');
        return;
    }
    if (!supabase) return;
    try {
        const { error } = await supabase.from('orders').update({ diagnosis_notes: notes }).eq('id', orderId);
        if (error) throw error;
        showToast('📋 Lab diagnosis logs updated.', 'success');
        document.getElementById('diagnosisModal')?.remove();
        loadDashboard();
    } catch (err) {
        showToast('Diagnosis update failed: ' + err.message, 'error');
    }
};
async function requestAdditionalParts(orderId) {
    // Build modal with inputs for part name and price
    const modal = document.createElement('div');
    modal.id = 'partModal';
    modal.className = 'fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4';
    modal.innerHTML = `
        <div class="bg-slate-900 border border-grayBorder rounded-2xl p-6 max-w-md w-full shadow-2xl">
            <h3 class="text-xl font-bold text-white mb-4">🔧 Request Additional Part</h3>
            <div class="space-y-4">
                <div>
                    <label class="text-sm text-grayText">Part Name</label>
                    <input type="text" id="partNameInput" class="w-full bg-navyBG border border-grayBorder rounded-lg p-2 text-white text-sm focus:border-teal-500 outline-none" placeholder="e.g. Battery, Screen, Charging Port..." />
                </div>
                <div>
                    <label class="text-sm text-grayText">Price (INR)</label>
                    <input type="number" id="partPriceInput" class="w-full bg-navyBG border border-grayBorder rounded-lg p-2 text-white text-sm focus:border-teal-500 outline-none" placeholder="Enter amount" min="0" step="1" />
                </div>
                <div class="flex gap-3 pt-2">
                    <button onclick="document.getElementById('partModal').remove()" class="flex-1 py-2 bg-gray-700 hover:bg-gray-600 rounded-xl text-white">Cancel</button>
                    <button onclick="confirmPart('${orderId}')" class="flex-1 py-2 bg-teal-600 hover:bg-teal-500 rounded-xl text-white font-bold">Add Part</button>
                </div>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
}

// Global function for the modal button
window.confirmPart = async function(orderId) {
    const partName = document.getElementById('partNameInput').value.trim();
    const price = parseFloat(document.getElementById('partPriceInput').value);
    if (!partName || isNaN(price) || price <= 0) {
        showToast('Please enter a valid part name and price.', 'error');
        return;
    }
    if (!supabase) return;
    try {
        const { data: ticket } = await supabase.from('orders').select('custom_quote_parts').eq('id', orderId).single();
        let existing = ticket.custom_quote_parts ? ticket.custom_quote_parts + '\n' : '';
        existing += `${partName},${price}`;
        const { error } = await supabase.from('orders').update({ custom_quote_parts: existing }).eq('id', orderId);
        if (error) throw error;
        showToast(`✅ Part "${partName}" added successfully.`, 'success');
        document.getElementById('partModal')?.remove();
        loadDashboard();
    } catch (err) {
        showToast('Request failed: ' + err.message, 'error');
    }
};

async function openQuotationEditor(orderId) {
    if (!supabase) return showToast('Supabase not connected', 'error');
    try {
        // Fetch order data
        const { data: order, error } = await supabase
            .from('orders')
            .select('*')
            .eq('id', orderId)
            .single();
        if (error) throw error;

        // Parse additional parts from custom_quote_parts (format: "PartName,Price\nPartName2,Price2")
        let additionalParts = [];
        if (order.custom_quote_parts) {
            additionalParts = order.custom_quote_parts.split('\n')
                .filter(line => line.trim() !== '')
                .map(line => {
                    const [name, price] = line.split(',').map(s => s.trim());
                    return { name, price: parseFloat(price) || 0 };
                });
        }

        const deviceName = getDeviceName(order.device_id) || order.device_other || 'Device';
        const partsTotal = order.parts_total || 0;
        const serviceFee = order.service_fee || 0;
        const diagnosisCharge = order.diagnosis_charge || 250;
        const additionalTotal = additionalParts.reduce((sum, p) => sum + p.price, 0);
        let totalQuoted = partsTotal + serviceFee + diagnosisCharge + additionalTotal;

        // Build modal
        const modal = document.createElement('div');
        modal.id = 'quotationEditorModal';
        modal.className = 'fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4 overflow-y-auto';
        modal.innerHTML = `
            <div class="bg-slate-900 border border-grayBorder rounded-2xl p-6 max-w-2xl w-full shadow-2xl my-8">
                <div class="flex justify-between items-center mb-4">
                    <h3 class="text-xl font-bold text-white">📋 Quotation Editor</h3>
                    <button onclick="document.getElementById('quotationEditorModal').remove()" class="text-gray-400 hover:text-white text-xl">✕</button>
                </div>
                <div class="text-sm text-gray-300 mb-4">
                    <span class="font-bold">${deviceName}</span> — ${order.repair_type_id ? getRepairLabel(order.repair_type_id) : order.repair_other || 'Repair'}
                </div>

                <!-- Static breakdown -->
                <div class="bg-navyBG/40 rounded-xl p-4 mb-4 space-y-2 text-sm">
                    <div class="flex justify-between"><span>Parts Total (INR)</span><span class="font-bold text-teal-400">₹${partsTotal.toFixed(2)}</span></div>
                    <div class="flex justify-between"><span>Service/Workmanship</span><span class="font-bold text-teal-400">₹${serviceFee.toFixed(2)}</span></div>
                    <div class="flex justify-between"><span>Diagnosis Charge (INR)</span><span class="font-bold text-teal-400">₹${diagnosisCharge.toFixed(2)}</span></div>
                </div>

                <!-- Additional Parts (editable) -->
                <div class="mb-4">
                    <label class="text-sm text-grayText font-bold block mb-2">Additional Parts (custom quote parts) – edit below</label>
                    <div id="additionalPartsList" class="space-y-2 max-h-40 overflow-y-auto">
                        ${additionalParts.length === 0 ? '<p class="text-gray-500 text-xs">No additional parts added yet.</p>' : ''}
                        ${additionalParts.map((p, idx) => `
                            <div class="flex items-center gap-2 bg-navyBG/30 p-2 rounded-lg border border-grayBorder/40">
                                <input type="text" value="${p.name}" class="part-name-input flex-1 bg-transparent border-none text-white text-sm focus:outline-none" data-idx="${idx}" placeholder="Part name" />
                                <input type="number" value="${p.price}" class="part-price-input w-24 bg-transparent border border-grayBorder/40 rounded px-2 py-1 text-white text-sm focus:border-teal-500 outline-none" data-idx="${idx}" step="0.01" min="0" />
                                <button onclick="removeAdditionalPart(${idx})" class="text-red-400 hover:text-red-300 text-sm font-bold px-2">✕</button>
                            </div>
                        `).join('')}
                    </div>
                    <div class="mt-2 flex gap-2">
                        <input type="text" id="newPartName" placeholder="Part name" class="flex-1 bg-navyBG border border-grayBorder rounded-lg p-2 text-sm text-white focus:border-teal-500 outline-none" />
                        <input type="number" id="newPartPrice" placeholder="Price" class="w-28 bg-navyBG border border-grayBorder rounded-lg p-2 text-sm text-white focus:border-teal-500 outline-none" step="0.01" min="0" />
                        <button onclick="addAdditionalPart()" class="bg-teal-600 hover:bg-teal-500 text-white px-4 py-2 rounded-lg text-sm font-bold">Add</button>
                    </div>
                    <p class="text-[10px] text-gray-500 mt-1">Add each part individually. They will appear in the list above.</p>
                </div>

                <!-- Total -->
                <div class="bg-teal-500/10 border border-teal-500/30 rounded-xl p-4 mb-4">
                    <div class="flex justify-between text-lg font-bold">
                        <span>Total Quoted Price</span>
                        <span id="quotationTotalDisplay" class="text-teal-400">₹${totalQuoted.toFixed(2)}</span>
                    </div>
                </div>

                <!-- Actions -->
                <div class="flex gap-3">
                    <button onclick="document.getElementById('quotationEditorModal').remove()" class="flex-1 py-2 bg-gray-700 hover:bg-gray-600 rounded-xl text-white">Cancel</button>
                    <button onclick="sendQuotationFromEditor('${orderId}')" class="flex-1 py-2 bg-teal-600 hover:bg-teal-500 rounded-xl text-white font-bold">Send Quotation</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);

        // Store additional parts globally for this modal instance
        window._quotationParts = additionalParts;

        // Set up live total update on input changes
        document.querySelectorAll('.part-price-input, .part-name-input').forEach(el => {
            el.addEventListener('input', updateQuotationTotal);
        });

    } catch (err) {
        showToast('Failed to load quotation: ' + err.message, 'error');
    }
}

// Helper to update total when part prices change
function updateQuotationTotal() {
    const priceInputs = document.querySelectorAll('.part-price-input');
    let total = 0;
    // Get static values from breakdown
    const partsTotal = parseFloat(document.querySelector('.bg-navyBG/40 .flex:first-child span:last-child')?.textContent?.replace('₹', '') || 0);
    const serviceFee = parseFloat(document.querySelector('.bg-navyBG/40 .flex:nth-child(2) span:last-child')?.textContent?.replace('₹', '') || 0);
    const diagnosis = parseFloat(document.querySelector('.bg-navyBG/40 .flex:nth-child(3) span:last-child')?.textContent?.replace('₹', '') || 0);
    let additional = 0;
    priceInputs.forEach(inp => {
        additional += parseFloat(inp.value) || 0;
    });
    const totalQuoted = partsTotal + serviceFee + diagnosis + additional;
    document.getElementById('quotationTotalDisplay').textContent = '₹' + totalQuoted.toFixed(2);
}

// Add additional part from the input fields
function addAdditionalPart() {
    const nameInput = document.getElementById('newPartName');
    const priceInput = document.getElementById('newPartPrice');
    const name = nameInput.value.trim();
    const price = parseFloat(priceInput.value);
    if (!name || isNaN(price) || price <= 0) {
        showToast('Enter a valid part name and price.', 'error');
        return;
    }
    const list = document.getElementById('additionalPartsList');
    const idx = list.children.length;
    const div = document.createElement('div');
    div.className = 'flex items-center gap-2 bg-navyBG/30 p-2 rounded-lg border border-grayBorder/40';
    div.innerHTML = `
        <input type="text" value="${name}" class="part-name-input flex-1 bg-transparent border-none text-white text-sm focus:outline-none" data-idx="${idx}" placeholder="Part name" />
        <input type="number" value="${price}" class="part-price-input w-24 bg-transparent border border-grayBorder/40 rounded px-2 py-1 text-white text-sm focus:border-teal-500 outline-none" data-idx="${idx}" step="0.01" min="0" />
        <button onclick="removeAdditionalPart(${idx})" class="text-red-400 hover:text-red-300 text-sm font-bold px-2">✕</button>
    `;
    list.appendChild(div);
    // Remove the "no parts" placeholder if present
    const placeholder = list.querySelector('p.text-gray-500');
    if (placeholder) placeholder.remove();
    // Add event listeners for live total update
    div.querySelectorAll('.part-price-input, .part-name-input').forEach(el => {
        el.addEventListener('input', updateQuotationTotal);
    });
    // Clear inputs
    nameInput.value = '';
    priceInput.value = '';
    updateQuotationTotal();
}

// Remove additional part by index
function removeAdditionalPart(idx) {
    const list = document.getElementById('additionalPartsList');
    const children = list.children;
    // Find the element with data-idx = idx
    for (let i = 0; i < children.length; i++) {
        const input = children[i].querySelector('.part-name-input');
        if (input && parseInt(input.dataset.idx) === idx) {
            children[i].remove();
            break;
        }
    }
    // Update the displayed total
    updateQuotationTotal();
}

// Send quotation from editor
async function sendQuotationFromEditor(orderId) {
    if (!supabase) return showToast('Supabase not connected', 'error');
    try {
        // Gather all part name and price pairs from the editor
        const nameInputs = document.querySelectorAll('.part-name-input');
        const priceInputs = document.querySelectorAll('.part-price-input');
        let parts = [];
        nameInputs.forEach((inp, i) => {
            const name = inp.value.trim();
            const price = parseFloat(priceInputs[i]?.value) || 0;
            if (name && price > 0) {
                parts.push({ name, price });
            }
        });
        // Format as "Name,Price\nName2,Price2"
        const customPartsString = parts.map(p => `${p.name},${p.price}`).join('\n');

        // Calculate total (we can read from the display)
        const totalDisplay = document.getElementById('quotationTotalDisplay');
        const total = parseFloat(totalDisplay.textContent.replace('₹', '')) || 0;

        // Update order
        const { error } = await supabase
            .from('orders')
            .update({
                custom_quote_parts: customPartsString || null,
                total_price: total,
                status: 'Quotation-Sent'
            })
            .eq('id', orderId);
        if (error) throw error;

        showToast('✉️ Quotation sent to customer for review.', 'success');
        document.getElementById('quotationEditorModal')?.remove();
        loadDashboard();
    } catch (err) {
        showToast('Failed to send quotation: ' + err.message, 'error');
    }
}

// Expose helper functions globally
window.openQuotationEditor = openQuotationEditor;
window.addAdditionalPart = addAdditionalPart;
window.removeAdditionalPart = removeAdditionalPart;
window.sendQuotationFromEditor = sendQuotationFromEditor;
window.updateQuotationTotal = updateQuotationTotal;
// Global function for the modal button
window.confirmQuotationFinal = async function(orderId) {
    const partsTotal = parseFloat(document.getElementById('quotePartsTotal').value) || 0;
    const serviceFee = parseFloat(document.getElementById('quoteServiceFee').value) || 0;
    const diagnosis = parseFloat(document.getElementById('quoteDiagnosis').value) || 0;
    const customParts = document.getElementById('quoteCustomParts').value.trim();
    const total = partsTotal + serviceFee + diagnosis;

    if (total <= 0) {
        showToast('Total must be greater than 0.', 'error');
        return;
    }

    if (!supabase) return;
    try {
        const updateData = {
            parts_total: partsTotal,
            service_fee: serviceFee,
            diagnosis_charge: diagnosis,
            total_price: total,
            custom_quote_parts: customParts || null,
            status: 'Quotation-Sent'
        };
        const { error } = await supabase
            .from('orders')
            .update(updateData)
            .eq('id', orderId);
        if (error) throw error;
        showToast('✉️ Quotation sent to customer!', 'success');
        document.getElementById('quotationModal')?.remove();
        loadDashboard();
    } catch (err) {
        showToast('Failed to send quotation: ' + err.message, 'error');
    }
};

async function confirmQuotation(orderId) {
    if (!supabase) return;
    try {
        const { error } = await supabase.from('orders').update({ status: 'Confirmed' }).eq('id', orderId);
        if (error) throw error;
        showToast('✅ Quotation approved! Repair work is starting.', 'success');
        loadDashboard();
    } catch (err) {
        showToast('Approval error: ' + err.message, 'error');
    }
}

async function rejectQuotation(orderId) {
    if (!supabase) return;
    try {
        const { error } = await supabase.from('orders').update({ status: 'Rejected' }).eq('id', orderId);
        if (error) throw error;
        showToast('❌ Quotation declined. Device reassembly & return requested.', 'info');
        loadDashboard();
    } catch (err) {
        showToast('Cancellation error: ' + err.message, 'error');
    }
}

// ─── 8. PROFILE MODIFICATION ENGINE ───
async function updateProfile() {
    if (!currentUser || !supabase) return showToast('Please login first.', 'error');
    const name = document.getElementById('profileName').value.trim();
    const phone = document.getElementById('profilePhone').value.trim();
    const address = document.getElementById('profileAddress').value.trim();
    const city = document.getElementById('profileCity').value;

    if (!city || city === 'Nagpur' || city === 'Amravati') {
        showToast('We currently only serve Wardha. Nagpur & Amravati coming soon!', 'error');
        return;
    }
    try {
        const { error } = await supabase
            .from('users')
            .update({ name: name, phone: phone, address: address + ', ' + city })
            .eq('id', currentUser.id);
        if (error) throw error;
        showToast('✅ Member profile updated successfully!', 'success');
        
        const navName = document.getElementById('navUserName');
        const mNavName = document.getElementById('mobileNavUserName');
        if (navName) navName.textContent = name || currentUser.email;
        if (mNavName) mNavName.textContent = name || currentUser.email;
    } catch (err) {
        showToast('❌ Update failed: ' + err.message, 'error');
    }
}

// ─── 9. LIVE DASHBOARD RENDERER ───
async function loadDashboard() {
    const container = document.getElementById('dashboardContent');
    if (!container) return;

    if (!currentUser) {
        container.innerHTML = `
            <div class="text-center text-grayText/60 py-12">
                <i class="fa-regular fa-circle-user text-6xl mb-4 block text-tealAccent"></i>
                <p class="text-lg font-medium text-white">Access Your Nagpur & Wardha Support Hub</p>
                <p class="text-xs text-grayText max-w-sm mx-auto mt-1">Check real-time estimates, update customer details, or track current ticket statuses.</p>
                <a href="login.html" class="btn-teal mt-4 inline-block">Login Now</a>
            </div>
        `;
        return;
    }

    // Populate profile inputs
    try {
        if (supabase) {
            const { data: userData } = await supabase
                .from('users')
                .select('name, phone, address')
                .eq('id', currentUser.id)
                .single();

            if (userData) {
                if (document.getElementById('profileName')) document.getElementById('profileName').value = userData.name || '';
                if (document.getElementById('profilePhone')) document.getElementById('profilePhone').value = userData.phone || '';
                
                const addrParts = (userData.address || '').split(', ');
                if (document.getElementById('profileAddress')) document.getElementById('profileAddress').value = addrParts.slice(0, -1).join(', ') || '';
                
                const cityPart = addrParts[addrParts.length - 1] || '';
                if (document.getElementById('profileCity') && ['Wardha', 'Nagpur', 'Amravati'].includes(cityPart)) {
                    document.getElementById('profileCity').value = cityPart;
                }
            }
        }
    } catch(e) {
        console.warn("Could not retrieve user info from table:", e);
    }

    const roles = await getUserRoles(currentUser.id);
    let activeRole = localStorage.getItem('activeRole');
    if (!activeRole || !roles.includes(activeRole)) {
        if (roles.includes('admin')) activeRole = 'admin';
        else if (roles.includes('coordinator')) activeRole = 'coordinator';
        else if (roles.includes('technician')) activeRole = 'technician';
        else if (roles.includes('repairmaster')) activeRole = 'repairmaster';
        else activeRole = 'customer';
        localStorage.setItem('activeRole', activeRole);
    }

    const isAdmin = activeRole === 'admin';
    const isCoordinator = activeRole === 'coordinator';
    const isTechnician = activeRole === 'technician';
    const isRepairMaster = activeRole === 'repairmaster';

    const roleBadge = document.getElementById('userRoleBadge');
    const roleDisplay = document.getElementById('roleDisplay');
    if (roleBadge && roleDisplay) {
        let roleName = 'Customer';
        if (isAdmin) roleName = 'Admin';
        else if (isCoordinator) roleName = 'Coordinator';
        else if (isTechnician) roleName = 'Technician';
        else if (isRepairMaster) roleName = 'RepairMaster';
        roleDisplay.textContent = roleName;
        roleBadge.classList.remove('hidden');
    }

    // Load orders
    let orders = [];
    if (supabase) {
        try {
            let query = supabase.from('orders').select('*');
            if (isAdmin) {
                // Load all
            } else if (isCoordinator) {
                query = query.in('status', ['Pending', 'Technician Assigned', 'RepairMaster Assigned', 'Pickup-Pending', 'With-RepairMaster', 'Quotation-Sent', 'Confirmed', 'Completed', 'Rejected', 'Cancelled']);
            } else if (isTechnician) {
                query = query.eq('technician_id', currentUser.id);
            } else if (isRepairMaster) {
                query = query.eq('repairmaster_id', currentUser.id);
            } else {
                query = query.eq('user_id', currentUser.id);
            }
            const { data, error } = await query.order('created_at', { ascending: false });
            if (error) throw error;
            orders = data || [];
        } catch (err) {
            console.warn("Using offline mock orders:", err);
            orders = [
                { id: 'm1', order_number: 'RM-MOCK-123', customer_name: 'Akash Chaware', customer_phone: '9876543210', device_other: 'Vivo V30 Pro', repair_other: 'Screen Replacement', total_price: 6300, status: 'Pending', created_at: new Date().toISOString() }
            ];
        }
    }

    // Update stats counters
    const total = orders.length;
    const pending = orders.filter(o => ['Pending', 'Technician Assigned', 'RepairMaster Assigned'].includes(o.status)).length;
    const inProgress = orders.filter(o => ['Pickup-Pending', 'With-RepairMaster', 'In-Progress'].includes(o.status)).length;
    const completed = orders.filter(o => ['Completed', 'Confirmed'].includes(o.status)).length;

    if (document.getElementById('statTotal')) document.getElementById('statTotal').textContent = total;
    if (document.getElementById('statPending')) document.getElementById('statPending').textContent = pending;
    if (document.getElementById('statInProgress')) document.getElementById('statInProgress').textContent = inProgress;
    if (document.getElementById('statCompleted')) document.getElementById('statCompleted').textContent = completed;

    window.allFetchedOrders = orders;

    function getDeviceName(deviceId) {
        if (!deviceId) return 'Generic Device';
        const dev = allDevices.find(d => String(d.id) === String(deviceId));
        return dev ? dev.name : 'Device';
    }
    window.getDeviceName = getDeviceName;

    function getRepairLabel(repairTypeId) {
        if (!repairTypeId) return 'Device Repair';
        const rt = allRepairTypes.find(r => String(r.id) === String(repairTypeId));
        return rt ? rt.label : 'Repair';
    }
    window.getRepairLabel = getRepairLabel;

    function buildSingleOrderCardHtml(o, isAdmin, isCoordinator, isTechnician, isRepairMaster, isGuestMode = false, isMatched = true) {
        const status = o.status || 'Pending';
        const statusClass = 'status-' + status.replace(/\s/g, '-');
        const deviceName = getDeviceName(o.device_id) !== 'Device' ? getDeviceName(o.device_id) : (o.device_other || 'Device');
        const repairLabel = getRepairLabel(o.repair_type_id) !== 'Repair' ? getRepairLabel(o.repair_type_id) : (o.repair_other || 'Repair');

        let actions = '';
        if (!isGuestMode) {
            if (isAdmin || isCoordinator) {
                if (status === 'Pending') {
                    actions += `
                        <button onclick="assignOrderRoles('${o.id}')" class="action-btn btn-assign">Assign Staff</button>
                    `;
                }
                if (isCoordinator) {
                    if (status === 'Pending' || status === 'Technician Assigned' || status === 'RepairMaster Assigned') {
                        actions += `
                            <button onclick="assignSelfAsTechnician('${o.id}')" class="action-btn btn-pickup">Take as Tech</button>
                            <button onclick="assignSelfAsRepairMaster('${o.id}')" class="action-btn btn-diagnose">Take as Master</button>
                        `;
                    }
                }
                if (['Technician Assigned', 'RepairMaster Assigned', 'Pickup-Pending', 'With-RepairMaster'].includes(status)) {
                    actions += `
                        <button onclick="sendQuotation('${o.id}')" class="action-btn btn-quote">Manage Price</button>
                    `;
                }
            }

            if (isTechnician && o.technician_id === currentUser?.id) {
                if (status === 'Technician Assigned') {
                    actions += `<button onclick="initiatePickup('${o.id}')" class="action-btn btn-pickup">Start Pickup</button>`;
                } else if (status === 'Pickup-Pending') {
                    actions += `
                        <div class="flex items-center gap-1 mt-1">
                            <input type="text" id="otp-${o.id}" placeholder="Pickup OTP" class="otp-input p-1.5 rounded-lg text-xs w-24 border border-teal-500/20 bg-slate-900 text-white mr-2"/>
                            <button onclick="verifyPickup('${o.id}', document.getElementById('otp-${o.id}').value)" class="action-btn btn-verify">Verify Handover</button>
                        </div>
                    `;
                } else if (status === 'Ready-For-Delivery') {
                    actions += `
                        <div class="flex items-center gap-1 mt-1">
                            <input type="text" id="delivery-otp-${o.id}" placeholder="Handover OTP" class="otp-input p-1.5 rounded-lg text-xs w-24 border border-teal-500/20 bg-slate-900 text-white mr-2"/>
                            <button onclick="closeTicket('${o.id}', document.getElementById('delivery-otp-${o.id}').value)" class="action-btn btn-verify">Verify Delivery</button>
                        </div>
                    `;
                }
            }

            if (isRepairMaster && o.repairmaster_id === currentUser?.id) {
                if (status === 'With-RepairMaster') {
                    actions += `
                        <button onclick="updateDiagnosis('${o.id}')" class="action-btn btn-diagnose">Diagnose Logs</button>
<button onclick="requestAdditionalParts('${o.id}')" class="action-btn btn-part">+ Add Part</button>
                    `;
                } else if (status === 'Confirmed') {
                    actions += `
                        <div class="flex flex-col gap-1 items-end">
                            <span class="text-xs text-emerald-400 font-bold"><i class="fa-solid fa-spinner fa-spin mr-1"></i> Under Active Work</span>
                            <button onclick="completeRepair('${o.id}')" class="action-btn btn-confirm py-1 px-3 mt-1 text-[11px]">Finish Repair</button>
                        </div>
                    `;
                }
            }
        }

        // Customer & Guest Actions
        const isClient = isGuestMode || (currentUser && o.user_id === currentUser.id && !isAdmin && !isCoordinator && !isTechnician && !isRepairMaster);
        if (isClient) {
            if (status === 'Quotation-Sent') {
                actions += `
                    <button onclick="confirmQuotation('${o.id}')" class="action-btn btn-confirm">Accept Quote</button>
                    <button onclick="rejectQuotation('${o.id}')" class="action-btn btn-reject">Decline</button>
                `;
            } else if (status === 'Awaiting-Payment' || (status === 'Rejected' && (o.total_price || 0) > 0)) {
                const labelPay = status === 'Rejected' ? `Pay Rejection Fee (₹${(o.total_price || 0).toLocaleString('en-IN')})` : `💳 Pay ₹${(o.total_price || 0).toLocaleString('en-IN')}`;
                actions += `
                    <button onclick="payForRepair('${o.id}', ${o.total_price || 0}, '${deviceName.replace(/'/g, "\\'")}')" class="action-btn btn-confirm">${labelPay}</button>
                `;
            } else if (status === 'Completed' || o.pickup_otp === 'VERIFIED') {
                // If they have not left a rating yet, allow writing a review!
                if (!o.customer_rating) {
                    actions += `
                        <div class="mt-4 p-4 rounded-xl bg-slate-900 border border-slate-800 text-left max-w-sm w-full font-display">
                            <p class="text-xs font-bold text-white mb-2"><i class="fa-regular fa-star text-teal mr-1"></i> Rate Your Doorstep Experience</p>
                            <div class="flex gap-1 mb-2">
                                <button onclick="this.parentElement.setAttribute('data-rating', '1'); Array.from(this.parentElement.children).forEach((el, idx) => el.className = idx < 1 ? 'text-amber-400' : 'text-gray-600')" class="text-gray-600 text-lg transition"><i class="fa-solid fa-star"></i></button>
                                <button onclick="this.parentElement.setAttribute('data-rating', '2'); Array.from(this.parentElement.children).forEach((el, idx) => el.className = idx < 2 ? 'text-amber-400' : 'text-gray-600')" class="text-gray-600 text-lg transition"><i class="fa-solid fa-star"></i></button>
                                <button onclick="this.parentElement.setAttribute('data-rating', '3'); Array.from(this.parentElement.children).forEach((el, idx) => el.className = idx < 3 ? 'text-amber-400' : 'text-gray-600')" class="text-gray-600 text-lg transition"><i class="fa-solid fa-star"></i></button>
                                <button onclick="this.parentElement.setAttribute('data-rating', '4'); Array.from(this.parentElement.children).forEach((el, idx) => el.className = idx < 4 ? 'text-amber-400' : 'text-gray-600')" class="text-gray-600 text-lg transition"><i class="fa-solid fa-star"></i></button>
                                <button onclick="this.parentElement.setAttribute('data-rating', '5'); Array.from(this.parentElement.children).forEach((el, idx) => el.className = idx < 5 ? 'text-amber-400' : 'text-gray-600')" class="text-gray-600 text-lg transition"><i class="fa-solid fa-star"></i></button>
                            </div>
                            <textarea id="review-text-${o.id}" placeholder="Any suggestions or feedback? (e.g. Excellent doorstep technician support in Wardha!)" class="w-full bg-slate-950 border border-slate-800 p-2 rounded-lg text-xs text-white outline-none mb-2" rows="2"></textarea>
                            <button onclick="const starVal = this.parentElement.querySelector('[data-rating]')?.getAttribute('data-rating') || '5'; submitOrderReview('${o.id}', parseInt(starVal), document.getElementById('review-text-${o.id}').value)" class="bg-teal px-3 py-1.5 rounded-lg text-slate-950 hover:bg-teal-500 font-bold text-[10px] transition">Submit Review</button>
                        </div>
                    `;
                } else {
                    actions += `
                        <div class="mt-2 text-xs text-amber-400 font-medium font-display">
                            <span>Your Rating: ${'⭐'.repeat(o.customer_rating)}</span>
                            ${o.customer_review ? `<p class="text-gray-400 italic mt-1">"${o.customer_review}"</p>` : ''}
                        </div>
                    `;
                }
            }
        }

        let otpNoticeHtml = '';
        if (isClient) {
            if (status === 'Pickup-Pending' && o.pickup_otp) {
                otpNoticeHtml = `
                    <div class="mt-3 p-3 rounded-lg bg-teal-500/10 border border-teal-500/20 text-xs text-teal-300 flex items-center justify-between">
                        <span>🔑 Pickup Handover Code: <span class="text-gray-400 italic">(Show to Technician)</span></span>
                        <strong class="text-sm text-white tracking-widest bg-teal-900/60 px-3 py-1 rounded border border-teal-500/30">${o.pickup_otp}</strong>
                    </div>
                `;
            } else if (status === 'Ready-For-Delivery' && o.pickup_otp && o.pickup_otp !== 'VERIFIED') {
                otpNoticeHtml = `
                    <div class="mt-3 p-3 rounded-lg bg-teal-500/10 border border-teal-500/20 text-xs text-teal-300 flex items-center justify-between">
                        <span>🔑 Delivery Handover Code: <span class="text-gray-400 italic">(Share with Technician)</span></span>
                        <strong class="text-sm text-white tracking-widest bg-teal-900/60 px-3 py-1 rounded border border-teal-500/30">${o.pickup_otp}</strong>
                    </div>
                `;
            }
        }

        let quotationHtml = '';
        if (status === 'Quotation-Sent' || o.total_price) {
            quotationHtml = `
                <div class="quotation-box mt-3 text-xs text-amber-400 bg-amber-500/5 border border-amber-500/20 p-3 rounded-lg">
                    <p class="font-bold uppercase tracking-wider mb-1"><i class="fa-solid fa-receipt mr-1"></i> Finalized Quotation Summary</p>
                    <p class="text-gray-300">Estimation finalized: <strong>₹${(o.total_price || 0).toLocaleString('en-IN')}</strong> ${status === 'Quotation-Sent' ? '<span class="text-amber-400">(Awaiting your acceptance)</span>' : ''}</p>
                    <div class="flex gap-4 text-[10px] text-gray-500 mt-1">
                        <span>🩺 Diagnosis: ₹${o.diagnosis_charge || 250}</span>
                        <span>🔧 Service/Workmanship: ₹${o.service_fee || 100}</span>
                        <span>🛠️ Parts: ₹${o.parts_total || 0}</span>
                    </div>
                </div>
            `;
        }

        // Live Workflow Tracking Indicator
        let workflowHtml = '';
        if (isClient) {
            const steps = [
                { name: 'Placed', active: true },
                { name: 'Assigned', active: ['Technician Assigned', 'Pickup-Pending', 'With-RepairMaster', 'Quotation-Sent', 'Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
                { name: 'Pickup', active: ['Pickup-Pending', 'With-RepairMaster', 'Quotation-Sent', 'Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
                { name: 'Lab Diagnosed', active: ['With-RepairMaster', 'Quotation-Sent', 'Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
                { name: 'Quoted', active: ['Quotation-Sent', 'Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
                { name: 'Repairing', active: ['Confirmed', 'Awaiting-Payment', 'Ready-For-Delivery', 'Completed'].includes(status) },
                { name: 'Paid', active: ['Ready-For-Delivery', 'Completed'].includes(status) },
                { name: 'Delivered', active: ['Completed'].includes(status) }
            ];
            workflowHtml = `
                <div class="mt-4 border-t border-slate-800 pt-3">
                    <p class="text-[10px] text-gray-500 font-bold uppercase tracking-widest mb-2"><i class="fa-solid fa-route mr-1 text-teal"></i> Live Tracking Workflow</p>
                    <div class="flex items-center justify-between text-[10px] text-center gap-1">
                        ${steps.map(s => `
                            <div class="flex-1">
                                <div class="h-1.5 w-full rounded-full mb-1 ${s.active ? 'bg-teal-500 shadow-sm shadow-teal-500/20' : 'bg-slate-800'}"></div>
                                <span class="${s.active ? 'text-teal-400 font-bold' : 'text-gray-500'} text-[9px] block leading-tight">${s.name}</span>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
        }

        // Role-Based Customer Metadata Panel (Access Control - Protect Customer Data)
        let metadataPanel = '';
        if (!isClient) {
            if (isAdmin || isCoordinator || isTechnician) {
                metadataPanel = `
                    <div class="mt-3 p-3 bg-slate-900/80 border border-slate-800 rounded-xl text-xs text-gray-300">
                        <p class="font-bold text-white mb-1 uppercase tracking-wider text-[10px] flex items-center gap-1 font-display">
                            <i class="fa-regular fa-user-circle text-teal"></i> DTC Customer Contact Details
                        </p>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-x-4 gap-y-1">
                            <div>👤 <strong>Name:</strong> ${o.customer_name || 'N/A'}</div>
                            <div>📞 <strong>Phone:</strong> ${o.customer_phone || 'N/A'}</div>
                            <div>✉️ <strong>Email:</strong> ${o.customer_email || 'N/A'}</div>
                            <div>📍 <strong>Address:</strong> ${o.address || 'N/A'}</div>
                            <div class="md:col-span-2">🎫 <strong>System Reference ID:</strong> ${o.order_number}</div>
                        </div>
                    </div>
                `;
            } else if (isRepairMaster) {
                metadataPanel = `
                    <div class="mt-3 p-3.5 bg-slate-950/60 border border-amber-500/10 rounded-xl text-xs text-gray-400">
                        <p class="font-bold text-amber-400 mb-1.5 uppercase tracking-wider text-[9px] flex items-center gap-1 font-display">
                            <i class="fa-solid fa-user-shield text-amber-500"></i> Customer Info Masked (Bench Protection)
                        </p>
                        <p class="text-[11px] leading-relaxed text-gray-500">
                            To ensure platform security and client privacy, customer direct identifiers and contact information are masked for Bench RepairMaster roles. Please coordinate logistics or customer approvals with the regional Hub Coordinator.
                        </p>
                    </div>
                `;
            }
        }

        // Dynamic visual indicator if the item is matched/unmatched
        const matchOpacityClass = isMatched ? '' : 'opacity-40 filter grayscale scale-95';

        return `
            <div class="order-card bg-slate-900/40 border border-slate-800/60 rounded-xl p-5 hover:border-teal-500/30 transition-all ${matchOpacityClass}">
                <div class="flex flex-wrap items-start justify-between gap-3">
                    <div class="flex-1 min-w-0">
                        <div class="flex items-center gap-3 flex-wrap">
                            <span class="text-lg font-bold text-white font-display">${deviceName}</span>
                            <span class="text-sm text-gray-500">—</span>
                            <span class="text-sm text-teal font-medium">${repairLabel}</span>
                            <span class="status-badge ${statusClass} text-xs">${status}</span>
                        </div>
                        <div class="text-xs text-gray-400 mt-1">
                            <span>ID: ${o.order_number}</span>
                            <span class="mx-2">•</span>
                            <span>📅 ${new Date(o.created_at).toLocaleDateString()}</span>
                            ${o.address ? `<span class="mx-2">•</span><span>📍 ${o.address}</span>` : ''}
                        </div>
                        ${o.photo_url ? `<img src="${o.photo_url}" class="mt-3 max-h-24 rounded-lg border border-slate-800" />` : ''}
                        ${o.diagnosis_notes ? `<p class="mt-2 text-xs text-gray-400 italic bg-slate-950/20 p-2 rounded border border-slate-800">Lab Diagnosis Logs: ${o.diagnosis_notes}</p>` : ''}
                        ${o.custom_quote_parts ? `<p class="mt-2 text-xs text-amber-300 italic bg-slate-950/20 p-2 rounded border border-slate-800">Requested Spare Parts: ${o.custom_quote_parts}</p>` : ''}
                        ${metadataPanel}
                        ${quotationHtml}
                        ${otpNoticeHtml}
                        ${workflowHtml}
                    </div>
                    <div class="flex flex-col items-end gap-2">
                        <span class="text-lg font-black text-teal">₹${(o.total_price || 0).toLocaleString('en-IN')}</span>
                        <div class="flex flex-wrap gap-1 justify-end">${actions}</div>
                    </div>
                </div>
            </div>
        `;
    }
    window.buildSingleOrderCardHtml = buildSingleOrderCardHtml;

    renderFilteredOrders();

    function renderFilteredOrders() {
        const container = document.getElementById('dashboardContent');
        if (!container) return;

        const activeRole = localStorage.getItem('activeRole') || 'customer';
        const isAdmin = activeRole === 'admin';
        const isCoordinator = activeRole === 'coordinator';
        const isTechnician = activeRole === 'technician';
        const isRepairMaster = activeRole === 'repairmaster';

        // Show or hide coordinator filters panel
        const filterPanel = document.getElementById('coordinatorFiltersPanel');
        if (filterPanel) {
            if (isCoordinator || isAdmin) {
                filterPanel.classList.remove('hidden');
            } else {
                filterPanel.classList.add('hidden');
            }
        }

        const searchQuery = document.getElementById('filterSearch')?.value.trim().toLowerCase() || '';
        const selectedHub = document.getElementById('filterHub')?.value || 'All';
        const selectedStatus = document.getElementById('filterStatus')?.value || 'All';

        const hasActiveFilter = !!(searchQuery || selectedHub !== 'All' || selectedStatus !== 'All');

        function isOrderMatching(o) {
            if (!hasActiveFilter) return true;

            let matchesSearch = true;
            if (searchQuery) {
                matchesSearch = (o.order_number || '').toLowerCase().includes(searchQuery) ||
                    (o.customer_name || '').toLowerCase().includes(searchQuery) ||
                    (o.customer_phone || '').toLowerCase().includes(searchQuery);
            }

            let matchesHub = true;
            if (selectedHub !== 'All') {
                matchesHub = (o.address || '').toLowerCase().includes(selectedHub.toLowerCase());
            }

            let matchesStatus = true;
            if (selectedStatus !== 'All') {
                if (selectedStatus === 'Pending') {
                    matchesStatus = o.status === 'Pending';
                } else if (selectedStatus === 'Pickup') {
                    matchesStatus = ['Technician Assigned', 'Pickup-Pending'].includes(o.status);
                } else if (selectedStatus === 'Lab') {
                    matchesStatus = o.status === 'With-RepairMaster';
                } else if (selectedStatus === 'Quotation') {
                    matchesStatus = o.status === 'Quotation-Sent';
                } else if (selectedStatus === 'Repairing') {
                    matchesStatus = o.status === 'Confirmed';
                } else if (selectedStatus === 'Payment') {
                    matchesStatus = o.status === 'Awaiting-Payment';
                } else if (selectedStatus === 'Delivery') {
                    matchesStatus = o.status === 'Ready-For-Delivery';
                } else if (selectedStatus === 'Completed') {
                    matchesStatus = o.status === 'Completed';
                } else if (selectedStatus === 'Rejected') {
                    matchesStatus = o.status === 'Rejected';
                }
            }

            return matchesSearch && matchesHub && matchesStatus;
        }

        let ordersToRender = [...window.allFetchedOrders];

        // Sort so matched orders are placed at the top, and unmatched "old logs" are kept at the bottom
        ordersToRender.sort((a, b) => {
            const matchA = isOrderMatching(a);
            const matchB = isOrderMatching(b);
            if (matchA && !matchB) return -1;
            if (!matchA && matchB) return 1;
            return new Date(b.created_at || 0) - new Date(a.created_at || 0);
        });

        if (ordersToRender.length === 0) {
            container.innerHTML = `
                <div class="text-center text-grayText/60 py-12">
                    <i class="fa-regular fa-folder-open text-5xl mb-3 text-tealAccent"></i>
                    <p class="text-base font-semibold text-white">No Tickets Available</p>
                    <p class="text-xs text-grayText">You do not have any active or historical tickets recorded on the platform.</p>
                </div>
            `;
            return;
        }

        let hasMatchedCount = ordersToRender.filter(isOrderMatching).length;
        let matchAlertHtml = '';
        if (hasActiveFilter && hasMatchedCount === 0) {
            matchAlertHtml = `
                <div class="bg-amber-500/10 border border-amber-500/20 text-amber-300 text-xs rounded-xl p-3 mb-4 flex items-center gap-2">
                    <i class="fa-solid fa-circle-exclamation text-amber-400"></i>
                    <span>No exact matches found. All historic/older logs are shown below.</span>
                </div>
            `;
        }

        let html = matchAlertHtml + `<div class="grid grid-cols-1 gap-4">`;
        ordersToRender.forEach(o => {
            const matched = isOrderMatching(o);
            html += buildSingleOrderCardHtml(o, isAdmin, isCoordinator, isTechnician, isRepairMaster, false, matched);
        });
        html += `</div>`;
        container.innerHTML = html;
    }
    window.renderFilteredOrders = renderFilteredOrders;

    function applyDashboardFilters() {
        renderFilteredOrders();
    }
    window.applyDashboardFilters = applyDashboardFilters;
}

// ─── 10. AUTH STATUS UPDATE & CUSTOM DRAWER/BELL CONTROLLERS ───

// Global Drawer toggle function
function toggleProfileDrawer() {
    const drawer = document.getElementById('profileSidebarDrawer');
    if (!drawer) return;
    drawer.classList.toggle('hidden');
}
window.toggleProfileDrawer = toggleProfileDrawer;

function enableDrawerEditMode() {
    const ro = document.getElementById('drawerReadOnlyView');
    const ed = document.getElementById('drawerEditableForm');
    if (ro) ro.classList.add('hidden');
    if (ed) ed.classList.remove('hidden');
}
window.enableDrawerEditMode = enableDrawerEditMode;

function disableDrawerEditMode() {
    const ro = document.getElementById('drawerReadOnlyView');
    const ed = document.getElementById('drawerEditableForm');
    if (ro) ro.classList.remove('hidden');
    if (ed) ed.classList.add('hidden');
}
window.disableDrawerEditMode = disableDrawerEditMode;

function toggleNotificationDropdown(event) {
    if (event) event.stopPropagation();
    const dropdown = document.getElementById('notificationDropdown');
    if (dropdown) dropdown.classList.toggle('hidden');
}
window.toggleNotificationDropdown = toggleNotificationDropdown;

function clearNotifications() {
    const badge = document.getElementById('navNotificationBadge');
    if (badge) badge.classList.add('hidden');
    showToast('Activity notifications marked as read.', 'success');
}
window.clearNotifications = clearNotifications;

// Listen for clicks outside dropdown to close it
document.addEventListener('click', function(e) {
    const dropdown = document.getElementById('notificationDropdown');
    const bellContainer = document.getElementById('notificationDropdownContainer');
    if (dropdown && !dropdown.classList.contains('hidden') && bellContainer && !bellContainer.contains(e.target)) {
        dropdown.classList.add('hidden');
    }
});

// Function to check and populate the track dropdown if user is logged in
async function checkAndPopulateTracker() {
    const trackOrderIdInput = document.getElementById('trackOrderId');
    const trackPhoneInput = document.getElementById('trackPhone');
    if (!supabase || !currentUser || !trackOrderIdInput || !trackPhoneInput) return;

    try {
        const { data: orders, error } = await supabase
            .from('orders')
            .select('*')
            .eq('user_id', currentUser.id)
            .order('created_at', { ascending: false });

        if (error) throw error;

        const { data: uData } = await supabase
            .from('users')
            .select('phone')
            .eq('id', currentUser.id)
            .single();

        if (uData && uData.phone) {
            trackPhoneInput.value = uData.phone;
        }

        if (orders && orders.length > 0) {
            const selectEl = document.createElement('select');
            selectEl.id = 'trackOrderId';
            selectEl.className = 'w-full bg-slate-950 border border-slate-800 p-3 rounded-xl text-sm text-white focus:border-teal-400 outline-none cursor-pointer';
            selectEl.required = true;

            orders.forEach(o => {
                const opt = document.createElement('option');
                opt.value = o.order_number;
                opt.textContent = `#${o.order_number} (${o.status || 'Pending'})`;
                selectEl.appendChild(opt);
            });

            const manualOpt = document.createElement('option');
            manualOpt.value = 'MANUAL';
            manualOpt.textContent = '✏️ Search another ticket...';
            selectEl.appendChild(manualOpt);

            const parentNode = trackOrderIdInput.parentNode;
            parentNode.replaceChild(selectEl, trackOrderIdInput);

            selectEl.onchange = function() {
                if (this.value === 'MANUAL') {
                    const txtInput = document.createElement('input');
                    txtInput.type = 'text';
                    txtInput.id = 'trackOrderId';
                    txtInput.placeholder = 'e.g. RM-REQ-JXK3D9';
                    txtInput.required = true;
                    txtInput.className = 'w-full bg-slate-950 border border-slate-800 p-3 rounded-xl text-sm text-white focus:border-teal-400 outline-none';
                    parentNode.replaceChild(txtInput, selectEl);
                }
            };
        }
    } catch (e) {
        console.warn("Tracker populating warning:", e);
    }
}
window.checkAndPopulateTracker = checkAndPopulateTracker;

async function updateNavForAuth(user) {
    const navLogin = document.getElementById('navLogin');
    const navSignup = document.getElementById('navSignup');
    const navUserInfo = document.getElementById('navUserInfo');
    
    const mNavLogin = document.getElementById('mobileNavLogin');
    const mNavSignup = document.getElementById('mobileNavSignup');
    const mNavUserInfo = document.getElementById('mobileNavUserInfo');

    if (user) {
        if (navLogin) navLogin.style.display = 'none';
        if (navSignup) navSignup.style.display = 'none';
        
        const username = user.user_metadata?.full_name || user.email.split('@')[0];
        const initials = username.substring(0, 2).toUpperCase();

        if (navUserInfo) {
            navUserInfo.classList.remove('hidden');
            navUserInfo.className = "flex items-center gap-4 flex-wrap";
            navUserInfo.innerHTML = `
                <!-- Dynamic Notification Bell -->
                <div class="relative inline-block text-left" id="notificationDropdownContainer">
                    <button onclick="toggleNotificationDropdown(event)" class="relative p-2 rounded-full text-gray-400 hover:text-white hover:bg-slate-800/40 focus:outline-none transition">
                        <i class="fa-regular fa-bell text-lg"></i>
                        <span id="navNotificationBadge" class="absolute top-1 right-1 inline-flex items-center justify-center px-1.5 py-0.5 text-[8px] font-black leading-none text-white bg-red-500 rounded-full">3</span>
                    </button>
                    <!-- Notification Dropdown -->
                    <div id="notificationDropdown" class="hidden absolute right-0 mt-3 w-80 bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl z-50 py-3 text-left">
                        <div class="px-4 py-2 border-b border-slate-800 flex justify-between items-center">
                            <span class="text-xs font-bold text-white uppercase tracking-wider">Activity Log Notifications</span>
                            <button onclick="clearNotifications()" class="text-[10px] text-teal-400 hover:underline">Mark read</button>
                        </div>
                        <div class="max-h-64 overflow-y-auto divide-y divide-slate-800/60" id="notificationList">
                            <div class="p-3 hover:bg-slate-800/30 transition text-xs">
                                <p class="text-white font-medium">📋 System Online</p>
                                <p class="text-[10px] text-gray-500 mt-0.5">Secure DTC connection initialized in Wardha Hub.</p>
                            </div>
                            <div class="p-3 hover:bg-slate-800/30 transition text-xs">
                                <p class="text-white font-medium">🔒 Privacy Checkpoint Active</p>
                                <p class="text-[10px] text-gray-500 mt-0.5">Device diagnostics verify maintenance logs safe.</p>
                            </div>
                            <div class="p-3 hover:bg-slate-800/30 transition text-xs">
                                <p class="text-white font-medium">✨ Account Verified</p>
                                <p class="text-[10px] text-gray-500 mt-0.5">Welcome to your dedicated dashboard.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Custom Avatar Menu trigger -->
                <div onclick="toggleProfileDrawer()" class="flex items-center gap-2.5 cursor-pointer hover:opacity-90 transition-all select-none">
                    <div class="w-8 h-8 rounded-full bg-teal-500/10 border border-teal-500/30 text-teal-400 font-bold text-xs flex items-center justify-center shadow-lg shadow-teal-500/5">
                        ${initials}
                    </div>
                    <span class="text-sm text-gray-300 font-bold hover:text-teal transition" id="navUserName">${username}</span>
                </div>

                <!-- Highly visible Logout Button directly on the Desktop Header -->
                <button onclick="logoutUser()" class="text-xs font-bold text-red-400 hover:text-red-300 border border-red-500/15 hover:border-red-500/30 px-3.5 py-1.5 rounded-xl bg-red-500/5 transition flex items-center gap-1.5">
                    <i class="fa-solid fa-power-off"></i> Logout
                </button>
            `;
        }

        if (mNavLogin) mNavLogin.style.display = 'none';
        if (mNavSignup) mNavSignup.style.display = 'none';

        // Remove old mobile dynamic info to avoid duplication
        const existingMUserInfo = document.getElementById('dynamicMobileUserInfo');
        if (existingMUserInfo) existingMUserInfo.remove();

        const mobileNav = document.getElementById('mobileNav');
        if (mobileNav) {
            const div = document.createElement('div');
            div.id = 'dynamicMobileUserInfo';
            div.className = 'mt-4 pt-4 border-t border-white/5 flex flex-col gap-3 items-center justify-center';
            div.innerHTML = `
                <div class="flex items-center gap-2.5">
                    <div class="w-9 h-9 rounded-full bg-teal-500/10 border border-teal-500/30 text-teal-400 font-bold text-sm flex items-center justify-center">
                        ${initials}
                    </div>
                    <div class="text-left">
                        <span class="text-sm text-white font-bold block">${username}</span>
                        <span class="text-[9px] text-teal-400 font-bold uppercase tracking-wider block">DTC Registered</span>
                    </div>
                </div>
                <div class="flex gap-2 w-full mt-2 px-4">
                    <button onclick="toggleProfileDrawer(); toggleMobileMenu();" class="flex-1 bg-slate-900 border border-slate-800 py-2 px-4 rounded-xl text-xs text-white font-bold flex items-center justify-center gap-1.5 hover:bg-slate-800 transition">
                        👤 Profile Details
                    </button>
                    <button onclick="logoutUser()" class="flex-1 bg-red-500/10 border border-red-500/20 py-2 px-4 rounded-xl text-xs text-red-400 font-bold flex items-center justify-center gap-1.5 hover:bg-red-500/20 transition">
                        <i class="fa-solid fa-power-off"></i> Logout
                    </button>
                </div>
            `;
            mobileNav.appendChild(div);
        }

        if (mNavUserInfo) {
            mNavUserInfo.classList.remove('hidden');
            mNavUserInfo.innerHTML = `
                <div class="mt-2 flex flex-col gap-2 items-center justify-center">
                    <button onclick="toggleProfileDrawer()" class="bg-slate-900 border border-slate-800 py-2 px-4 rounded-xl text-xs text-white font-bold">
                        👤 View &amp; Edit Profile
                    </button>
                </div>
            `;
        }

        // Setup dynamic profile sidebar drawer container
        let drawer = document.getElementById('profileSidebarDrawer');
        if (!drawer) {
            drawer = document.createElement('div');
            drawer.id = 'profileSidebarDrawer';
            drawer.className = 'fixed inset-y-0 right-0 w-96 bg-slate-900/95 border-l border-slate-800 shadow-2xl z-50 transform translate-x-full transition-transform duration-300 ease-in-out p-6 flex flex-col justify-between hidden';
            document.body.appendChild(drawer);
        }

        // Fetch user metadata/profile table to populate drawer dynamically
        let userDbData = { name: username, phone: user.user_metadata?.phone || 'N/A', address: 'Wardha, Maharashtra' };
        try {
            const { data: dbUser, error: dbErr } = await supabase.from('users').select('*').eq('id', user.id).single();
            if (dbUser) {
                userDbData = dbUser;
            } else {
                // Self-healing database insert if the signup failed to insert!
                const newProfile = {
                    id: user.id,
                    email: user.email,
                    name: username,
                    phone: user.user_metadata?.phone || 'N/A',
                    address: user.user_metadata?.city || 'Wardha'
                };
                await supabase.from('users').upsert([newProfile]);
                userDbData = newProfile;
                
                // Self-healing role insert if missing
                const { data: rolesCheck } = await supabase.from('user_roles').select('*').eq('user_id', user.id);
                if (!rolesCheck || rolesCheck.length === 0) {
                    await supabase.from('user_roles').upsert([{
                        user_id: user.id,
                        role_id: 5 // Customer
                    }]);
                }
            }
        } catch (e) {
            console.warn("Could not fetch or auto-create database user profile:", e);
        }

        drawer.innerHTML = `
            <div>
                <!-- Header -->
                <div class="flex items-center justify-between border-b border-slate-800 pb-4 mb-6">
                    <h3 class="text-base font-bold text-white flex items-center gap-2 font-display">
                        <i class="fa-regular fa-address-card text-teal"></i> Account Profile
                    </h3>
                    <button onclick="toggleProfileDrawer()" class="text-gray-400 hover:text-white text-base font-bold">✕</button>
                </div>

                <!-- Avatar Section -->
                <div class="text-center mb-6">
                    <div class="w-16 h-16 bg-teal-500/10 border border-teal-500/20 text-teal-400 rounded-full flex items-center justify-center text-2xl font-black mx-auto mb-2 shadow-lg shadow-teal-500/5">
                        ${initials}
                    </div>
                    <h4 class="text-sm font-bold text-white" id="drawerProfileEmail">${user.email}</h4>
                    <span class="inline-block bg-teal-500/10 text-teal-400 text-[9px] uppercase font-bold px-2.5 py-0.5 rounded-full mt-1.5">DTC Registered Member</span>
                </div>

                <!-- Profile Data Fields -->
                <div class="space-y-4">
                    <!-- Read Only View -->
                    <div id="drawerReadOnlyView" class="space-y-4">
                        <div class="bg-slate-950/40 p-3.5 rounded-xl border border-slate-800/60 text-left">
                            <span class="text-[9px] text-gray-500 uppercase font-black tracking-wider block">Full Name</span>
                            <p class="text-xs text-white font-medium mt-0.5" id="drawerLabelName">${userDbData.name}</p>
                        </div>
                        <div class="bg-slate-950/40 p-3.5 rounded-xl border border-slate-800/60 text-left">
                            <span class="text-[9px] text-gray-500 uppercase font-black tracking-wider block">Mobile Number</span>
                            <p class="text-xs text-white font-medium mt-0.5" id="drawerLabelPhone">${userDbData.phone}</p>
                        </div>
                        <div class="bg-slate-950/40 p-3.5 rounded-xl border border-slate-800/60 text-left">
                            <span class="text-[9px] text-gray-500 uppercase font-black tracking-wider block">Doorstep Address</span>
                            <p class="text-xs text-white font-medium mt-0.5" id="drawerLabelAddress">${userDbData.address}</p>
                        </div>
                        <div class="bg-slate-950/40 p-3.5 rounded-xl border border-slate-800/60 text-left">
                            <span class="text-[9px] text-gray-500 uppercase font-black tracking-wider block">Assigned Hub</span>
                            <p class="text-xs text-teal-400 font-bold mt-0.5"><i class="fa-solid fa-location-dot text-[10px] mr-1"></i> Wardha Hub (Active)</p>
                        </div>
                        <button onclick="enableDrawerEditMode()" class="w-full bg-slate-800/60 hover:bg-slate-800 text-teal-400 hover:text-teal-300 text-xs font-bold py-3 rounded-xl border border-teal-500/10 mt-3 transition flex items-center justify-center gap-1.5">
                            <i class="fa-regular fa-edit"></i> Edit Profile Details
                        </button>
                    </div>

                    <!-- Edit Form (Hidden by default) -->
                    <div id="drawerEditableForm" class="hidden space-y-4">
                        <div class="text-left">
                            <label class="text-[10px] text-gray-400 uppercase font-bold tracking-wider block mb-1">Full Name</label>
                            <input type="text" id="profileName" value="${userDbData.name}" class="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white text-xs focus:border-teal outline-none transition">
                        </div>
                        <div class="text-left">
                            <label class="text-[10px] text-gray-400 uppercase font-bold tracking-wider block mb-1">Mobile Number</label>
                            <input type="tel" id="profilePhone" value="${userDbData.phone}" class="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white text-xs focus:border-teal outline-none transition">
                        </div>
                        <div class="text-left">
                            <label class="text-[10px] text-gray-400 uppercase font-bold tracking-wider block mb-1">Doorstep Address</label>
                            <input type="text" id="profileAddress" value="${userDbData.address}" class="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white text-xs focus:border-teal outline-none transition">
                        </div>
                        <div class="text-left">
                            <label class="text-[10px] text-gray-400 uppercase font-bold tracking-wider block mb-1">Hub Location</label>
                            <select id="profileCity" class="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white text-xs focus:border-teal outline-none transition cursor-pointer">
                                <option value="Wardha">Wardha Hub (Active)</option>
                                <option value="Nagpur" disabled>Nagpur Hub (Coming soon)</option>
                                <option value="Amravati" disabled>Amravati Hub (Coming soon)</option>
                            </select>
                        </div>
                        <div class="flex gap-2 pt-2">
                            <button onclick="updateProfile()" class="flex-1 bg-teal-600 hover:bg-teal-500 text-slate-950 text-xs font-bold py-2.5 rounded-xl transition flex items-center justify-center gap-1">
                                <i class="fa-regular fa-circle-check"></i> Save
                            </button>
                            <button onclick="disableDrawerEditMode()" class="flex-1 bg-slate-800 hover:bg-slate-700 text-white text-xs font-bold py-2.5 rounded-xl border border-slate-700 transition">
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Footer Section -->
            <button onclick="logoutUser()" class="w-full bg-red-500/10 hover:bg-red-500/20 text-red-400 text-xs font-bold py-3 rounded-xl border border-red-500/20 transition mt-6 flex items-center justify-center gap-2">
                <i class="fa-solid fa-power-off"></i> Log Out Account
            </button>
        `;

        try {
            const roles = await getUserRoles(user.id);
            localStorage.setItem('allUserRoles', JSON.stringify(roles));
            
            let activeRole = localStorage.getItem('activeRole');
            if (!activeRole || !roles.includes(activeRole)) {
                if (roles.includes('admin')) activeRole = 'admin';
                else if (roles.includes('coordinator')) activeRole = 'coordinator';
                else if (roles.includes('technician')) activeRole = 'technician';
                else if (roles.includes('repairmaster')) activeRole = 'repairmaster';
                else activeRole = 'customer';
                localStorage.setItem('activeRole', activeRole);
            }
            renderRoleSelector(roles, activeRole);
            
            // Populate check/track widgets elegantly
            await checkAndPopulateTracker();
        } catch (e) {
            console.warn("Could not load roles for nav update:", e);
        }
    } else {
        if (navLogin) navLogin.style.display = 'inline-block';
        if (navSignup) navSignup.style.display = 'inline-block';
        if (navUserInfo) navUserInfo.classList.add('hidden');
        localStorage.removeItem('allUserRoles');
        localStorage.removeItem('activeRole');

        if (mNavLogin) mNavLogin.style.display = 'inline-block';
        if (mNavSignup) mNavSignup.style.display = 'inline-block';
        if (mNavUserInfo) mNavUserInfo.classList.add('hidden');

        const existingMUserInfo = document.getElementById('dynamicMobileUserInfo');
        if (existingMUserInfo) existingMUserInfo.remove();

        let drawer = document.getElementById('profileSidebarDrawer');
        if (drawer) drawer.classList.add('hidden');
    }
}

function renderRoleSelector(roles, activeRole) {
    const navUserInfo = document.getElementById('navUserInfo');
    const mNavUserInfo = document.getElementById('mobileNavUserInfo');
    
    if (!navUserInfo) return;

    let switcher = document.getElementById('navRoleSwitcher');
    if (!switcher) {
        switcher = document.createElement('div');
        switcher.id = 'navRoleSwitcher';
        switcher.className = 'flex items-center gap-2 bg-slate-900/80 border border-teal-500/30 px-3 py-1.5 rounded-xl text-xs text-white';
        navUserInfo.appendChild(switcher);
    }

    if (roles.length > 1) {
        const optionsHtml = roles.map(r => `
            <option value="${r}" ${r === activeRole ? 'selected' : ''}>${r.toUpperCase()}</option>
        `).join('');
        switcher.innerHTML = `
            <label class="text-[9px] text-teal-400 font-bold uppercase tracking-wider"><i class="fa-solid fa-arrows-spin mr-1"></i> Dashboard:</label>
            <select onchange="switchActiveRole(this.value)" class="bg-transparent text-white font-bold outline-none cursor-pointer">
                ${optionsHtml}
            </select>
        `;
    } else {
        switcher.innerHTML = `
            <span class="text-teal-400 font-bold tracking-wider uppercase text-[10px]"><i class="fa-solid fa-user-shield mr-1"></i> ${activeRole}</span>
        `;
    }

    if (mNavUserInfo) {
        let mSwitcher = document.getElementById('mobileNavRoleSwitcher');
        if (!mSwitcher) {
            mSwitcher = document.createElement('div');
            mSwitcher.id = 'mobileNavRoleSwitcher';
            mSwitcher.className = 'mt-2 flex items-center justify-center gap-2 bg-slate-900/80 border border-teal-500/30 px-3 py-1.5 rounded-xl text-xs text-white mx-auto max-w-[200px]';
            mNavUserInfo.appendChild(mSwitcher);
        }
        if (roles.length > 1) {
            const mOptionsHtml = roles.map(r => `
                <option value="${r}" ${r === activeRole ? 'selected' : ''}>${r.toUpperCase()}</option>
            `).join('');
            mSwitcher.innerHTML = `
                <label class="text-[9px] text-teal-400 font-bold uppercase tracking-wider">Dashboard:</label>
                <select onchange="switchActiveRole(this.value)" class="bg-transparent text-white font-bold outline-none cursor-pointer">
                    ${mOptionsHtml}
                </select>
            `;
        } else {
            mSwitcher.innerHTML = `
                <span class="text-teal-400 font-bold tracking-wider uppercase text-[10px]"><i class="fa-solid fa-user-shield mr-1"></i> ${activeRole}</span>
            `;
        }
    }
}

function switchActiveRole(newRole) {
    localStorage.setItem('activeRole', newRole);
    showToast(`Navigating to ${newRole.toUpperCase()} Dashboard`, 'success');
    setTimeout(() => {
        if (window.location.href.includes('dashboard.html')) {
            window.location.reload();
        } else {
            window.location.href = 'dashboard.html';
        }
    }, 1000);
}

window.switchActiveRole = switchActiveRole;

async function completeRepair(orderId) {
    if (!supabase) return;
    try {
        const { error } = await supabase.from('orders').update({ status: 'Completed' }).eq('id', orderId);
        if (error) throw error;
        showToast('🎉 Repair completed! Ready for delivery.', 'success');
        loadDashboard();
    } catch (err) {
        showToast('Failed to complete repair: ' + err.message, 'error');
    }
}

async function payForRepair(orderId, amount, deviceName) {
    // Show a premium payment confirmation gateway popup!
    const modal = document.createElement('div');
    modal.id = 'paymentGatewayModal';
    modal.className = 'fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4';
    modal.innerHTML = `
        <div class="bg-slate-900 border border-teal-500/30 p-6 rounded-2xl max-w-md w-full shadow-2xl relative text-left">
            <button onclick="document.getElementById('paymentGatewayModal').remove()" class="absolute top-4 right-4 text-gray-400 hover:text-white">✕</button>
            <div class="text-center mb-6">
                <div class="w-16 h-16 bg-teal-500/10 border border-teal-500/20 text-teal-400 rounded-full flex items-center justify-center text-2xl mx-auto mb-3">
                    <i class="fa-solid fa-shield-halved"></i>
                </div>
                <h3 class="text-lg font-bold text-white">Secure Gateway Payment</h3>
                <p class="text-xs text-gray-400">RepairMaster DTC Escrow Channel</p>
            </div>
            
            <div class="bg-slate-950 border border-slate-800 p-4 rounded-xl mb-6 space-y-2 text-sm text-gray-300">
                <div class="flex justify-between"><span>Device:</span><span class="text-white font-bold">${deviceName}</span></div>
                <div class="flex justify-between"><span>Amount to Pay:</span><span class="text-teal-400 font-black">₹${amount.toLocaleString('en-IN')}</span></div>
            </div>
            
            <div class="space-y-4">
                <div>
                    <label class="text-[10px] text-gray-400 font-bold uppercase block mb-1">Select Payment Method</label>
                    <select id="payMethod" class="w-full bg-slate-950 border border-slate-800 p-3 rounded-xl text-sm text-white focus:border-teal-500/50 outline-none">
                        <option value="upi">UPI / Scan GPay / PhonePe</option>
                        <option value="card">Credit / Debit Card</option>
                        <option value="cod">Cash on Delivery</option>
                    </select>
                </div>
                
                <div id="upiQRCodeBlock" class="text-center p-4 bg-white/5 rounded-xl border border-slate-800 flex flex-col items-center gap-3">
                    <i class="fa-solid fa-qrcode text-5xl text-teal-400"></i>
                    <p class="text-xs text-teal-300 font-bold">BHIM UPI Dynamic QR Generated</p>
                    <p class="text-[10px] text-gray-400">Scan to pay ₹${amount.toLocaleString('en-IN')} securely via any UPI App</p>
                </div>
            </div>
            
            <button onclick="executePayment('${orderId}')" class="bg-teal-600 hover:bg-teal-500 text-white w-full mt-6 py-3 rounded-xl font-bold text-sm transition shadow-lg shadow-teal-500/20">Confirm &amp; Validate Transaction</button>
        </div>
    `;
    document.body.appendChild(modal);
}

async function executePayment(orderId) {
    if (!supabase) return;
    try {
        const handoverOtp = Math.floor(1000 + Math.random() * 9000).toString(); // 4-digit handover code
        const { error } = await supabase.from('orders').update({
            pickup_otp: handoverOtp
        }).eq('id', orderId);
        if (error) throw error;
        
        document.getElementById('paymentGatewayModal')?.remove();
        showToast('💳 Payment Successful! Your Secure Handover OTP is generated.', 'success');
        loadDashboard();
    } catch (err) {
        showToast('Failed to log payment: ' + err.message, 'error');
    }
}

async function closeTicket(orderId, enteredOtp) {
    if (!supabase) return;
    try {
        const { data, error } = await supabase.from('orders').select('pickup_otp').eq('id', orderId).single();
        if (error) throw error;
        if (data.pickup_otp !== enteredOtp) {
            showToast('❌ Invalid verification OTP. Authentication failed.', 'error');
            return;
        }
        await supabase.from('orders').update({ pickup_otp: 'VERIFIED', status: 'Completed' }).eq('id', orderId);
        showToast('🔒 Delivery Handover Verified! Ticket Closed successfully.', 'success');
        loadDashboard();
    } catch (err) {
        showToast('Verification failed: ' + err.message, 'error');
    }
}

async function logoutUser() {
    try {
        if (supabase) await supabase.auth.signOut();
        currentUser = null;
        updateNavForAuth(null);
        showToast('Successfully logged out.', 'info');
        setTimeout(() => { window.location.href = 'index.html'; }, 1000);
    } catch (err) {
        showToast('Logout error: ' + err.message, 'error');
    }
}

// ─── 11. REQUEST DROPDOWN HELPERS ───
function populateRequestBrands() {
    const select = document.getElementById('reqBrand');
    if (!select) return;
    select.innerHTML = '<option value="">— Select Brand —</option>';
    allBrands.forEach(b => {
        const opt = document.createElement('option');
        opt.value = b.id;
        opt.textContent = b.name;
        select.appendChild(opt);
    });
}

function updateReqModels() {
    const brandId = document.getElementById('reqBrand').value;
    const modelSelect = document.getElementById('reqModel');
    if (!modelSelect) return;
    modelSelect.innerHTML = '<option value="">— Select Model —</option>';
    
    if (!brandId) return;
    const devices = allDevices.filter(d => String(d.brand_id) === String(brandId));
    devices.forEach(d => {
        const opt = document.createElement('option');
        opt.value = d.id;
        opt.textContent = d.name;
        modelSelect.appendChild(opt);
    });
    updateReqRepairTypes();
}

function updateReqRepairTypes() {
    const modelId = document.getElementById('reqModel').value;
    const repairSelect = document.getElementById('reqRepairType');
    if (!repairSelect) return;
    repairSelect.innerHTML = '<option value="">— Select Repair Type —</option>';
    
    if (!modelId) return;
    allRepairTypes.forEach(rt => {
        const opt = document.createElement('option');
        opt.value = rt.id;
        opt.textContent = rt.label || rt.name;
        repairSelect.appendChild(opt);
    });
    showRequestEstimate();
}

function toggleOther(fieldId) {
    const otherDiv = document.getElementById(fieldId + 'Other') || document.getElementById(fieldId.replace('Type', '') + 'Other');
    if (otherDiv) {
        otherDiv.classList.toggle('visible');
        const select = document.getElementById(fieldId);
        if (select) select.disabled = otherDiv.classList.contains('visible');
    }
    showRequestEstimate();
}

function showRequestEstimate() {
    const brandSelect = document.getElementById('reqBrand');
    const modelSelect = document.getElementById('reqModel');
    const repairSelect = document.getElementById('reqRepairType');
    const estimateDiv = document.getElementById('requestEstimate');
    if (!brandSelect || !modelSelect || !repairSelect || !estimateDiv) return;

    const brandId = brandSelect.value;
    const modelId = modelSelect.value;
    const repairTypeId = repairSelect.value;
    const isOtherBrand = document.getElementById('reqBrandOther')?.classList.contains('visible');
    const isOtherModel = document.getElementById('reqModelOther')?.classList.contains('visible');
    const isOtherRepair = document.getElementById('reqRepairOther')?.classList.contains('visible');

    if (!isOtherBrand && !brandId) {
        estimateDiv.classList.add('hidden');
        return;
    }
    if (!isOtherModel && !modelId) {
        estimateDiv.classList.add('hidden');
        return;
    }
    if (!isOtherRepair && !repairTypeId) {
        estimateDiv.classList.add('hidden');
        return;
    }

    let totalPartsPrice = 0;
    let partsFound = false;
    if (!isOtherBrand && !isOtherModel && !isOtherRepair && brandId && modelId && repairTypeId) {
        const parts = allParts.filter(p => String(p.device_id) === String(modelId) && String(p.repair_type_id) === String(repairTypeId));
        if (parts && parts.length > 0) {
            partsFound = true;
            const qualityMultiplier = 0.7;
            parts.forEach(part => { totalPartsPrice += part.price * qualityMultiplier; });
        }
    }
    
    const discountedParts = totalPartsPrice * 0.9;
    const serviceFee = discountedParts > 0 ? (discountedParts * 0.15) : 100.00;
    const diagnosisCharge = 250;
    const total = discountedParts + serviceFee + diagnosisCharge;

    document.getElementById('reqPartsTotal').textContent = '₹' + discountedParts.toFixed(2) + (discountedParts === 0 ? ' (No parts selected)' : '');
    document.getElementById('reqServiceFee').textContent = '₹' + serviceFee.toFixed(2) + (discountedParts === 0 ? ' (Minimum service fee)' : '');
    document.getElementById('reqDiagnosis').textContent = '₹' + diagnosisCharge.toFixed(2);
    document.getElementById('reqTotal').textContent = '₹' + total.toFixed(2);
    estimateDiv.classList.remove('hidden');
}

// ─── 12. LOGINS & AUTHS ───
async function loginUser(e) {
    e.preventDefault();
    if (!supabase) return showToast('Supabase Client disconnected', 'error');
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    try {
        const { data, error } = await supabase.auth.signInWithPassword({ email, password });
        if (error) throw error;
        currentUser = data.user;
        updateNavForAuth(currentUser);
        showToast('Welcome back, ' + (currentUser.user_metadata?.full_name || 'Member') + '!', 'success');
        setTimeout(() => { window.location.href = 'dashboard.html'; }, 1000);
    } catch (err) {
        showToast('Login Failed: ' + err.message, 'error');
    }
}

async function signupUser(e) {
    e.preventDefault();
    if (!supabase) return showToast('Supabase Client disconnected', 'error');
    const name = document.getElementById('signupName').value.trim();
    const email = document.getElementById('signupEmail').value.trim();
    const phone = document.getElementById('signupPhone').value.trim();
    const password = document.getElementById('signupPassword').value;
    const city = document.getElementById('signupCity')?.value || 'Wardha';
    const selectedRole = document.getElementById('signupRole')?.value || '5';

    try {
        const { data, error } = await supabase.auth.signUp({
            email,
            password,
            options: { data: { full_name: name, phone: phone, city: city } }
        });
        if (error) throw error;
        if (data.user) {
            // Wait for profile upsert (robust, avoids duplicate trigger crash, and catches RLS errors gracefully)
            try {
                await supabase.from('users').upsert([{
                    id: data.user.id,
                    email: email,
                    name: name,
                    phone: phone,
                    address: city
                }]);
                await supabase.from('user_roles').upsert([{
                    user_id: data.user.id,
                    role_id: parseInt(selectedRole)
                }]);
            } catch (dbErr) {
                console.warn("Could not write profile to public database directly on signup (will auto-heal on login):", dbErr);
            }
        }
        showToast('Registration success! Redirecting to login...', 'success');
        setTimeout(() => { window.location.href = 'login.html'; }, 2000);
    } catch (err) {
        showToast('Signup Failed: ' + err.message, 'error');
    }
}

async function signInWithGoogle() {
    if (!supabase) return;
    try {
        const redirectUrl = window.location.origin + window.location.pathname.replace(/\/[^\/]*$/, '/dashboard.html');
        const { error } = await supabase.auth.signInWithOAuth({
            provider: 'google',
            options: { redirectTo: redirectUrl }
        });
        if (error) throw error;
    } catch (err) {
        showToast('Google login error: ' + err.message, 'error');
    }
}

function toggleMobileMenu() {
    const mobileNav = document.getElementById('mobileNav');
    if (mobileNav) mobileNav.classList.toggle('hidden');
}

// Carousel controls
let currentSlide = 0;
const totalSlides = 5;
let slideInterval;

function goToSlide(index) {
    const slides = document.querySelectorAll('.carousel-slide');
    const dots = document.querySelectorAll('.carousel-dot');
    if (slides.length > 0) {
        slides.forEach((s, i) => s.classList.toggle('active', i === index));
    }
    if (dots.length > 0) {
        dots.forEach((d, i) => d.classList.toggle('active', i === index));
    }
    currentSlide = index;
}

function nextSlide() {
    goToSlide((currentSlide + 1) % totalSlides);
}

function startCarousel() {
    slideInterval = setInterval(nextSlide, 6000);
}

// ─── 13. APPLICATION INITIALIZER ───
document.addEventListener('DOMContentLoaded', async function() {
    // 1. Fetch current path context
    const path = window.location.pathname.toLowerCase();
    const isLogin = path.includes('login');
    const isSignup = path.includes('signup');
    const isDashboard = path.includes('dashboard');
    const isRequest = path.includes('request');
    const isApp = path.includes('app');

    // 2. Hydrate database parts catalogs and offers
    await loadCatalog();

    if (document.getElementById('brandSelect')) {
        populateBrands();
    }
    if (document.getElementById('reqBrand')) {
        populateRequestBrands();
    }
    
    await fetchOffers();

    if (document.querySelectorAll('.carousel-slide').length > 0) {
        startCarousel();
    }

    // 3. Authenticate Session
    if (supabase) {
        const session = await supabase.auth.getSession();
        if (session.data?.session) {
            currentUser = session.data.session.user;
            updateNavForAuth(currentUser);
            
            // Redirect logged-in users away from Auth forms
            if (isLogin || isSignup) {
                window.location.href = 'dashboard.html';
            }
            if (isDashboard) {
                await loadDashboard();
            }
        } else {
            updateNavForAuth(null);
            // Protect dashboard page
            if (isDashboard) {
                window.location.href = 'login.html';
            }
        }
    } else {
        updateNavForAuth(null);
    }
});

async function trackOrderGuest(e) {
    e.preventDefault();
    if (!supabase) return showToast('Supabase Client disconnected', 'error');
    let orderId = document.getElementById('trackOrderId').value.trim();
    if (orderId.startsWith('#')) {
        orderId = orderId.substring(1).trim();
    }
    let phone = document.getElementById('trackPhone').value.trim();

    // Standardize phone search by stripping any non-numeric characters
    const cleanPhone = phone.replace(/[^0-9]/g, '');

    const resultDiv = document.getElementById('guestTrackResult');
    if (!resultDiv) return;

    try {
        resultDiv.classList.remove('hidden');
        resultDiv.innerHTML = `<div class="text-center py-4 text-xs text-teal-400"><i class="fa-solid fa-spinner fa-spin mr-1"></i> Searching for request...</div>`;

        // Fetch from orders
        const { data: orders, error } = await supabase
            .from('orders')
            .select('*')
            .eq('order_number', orderId);

        if (error) throw error;

        if (!orders || orders.length === 0) {
            resultDiv.innerHTML = `<div class="p-4 bg-red-500/10 border border-red-500/20 text-red-400 rounded-xl text-center font-bold text-xs">❌ No order matching reference #${orderId} was found.</div>`;
            return;
        }

        const o = orders[0];
        const dbPhone = (o.customer_phone || '').replace(/[^0-9]/g, '');

        if (dbPhone.slice(-10) !== cleanPhone.slice(-10)) {
            resultDiv.innerHTML = `<div class="p-4 bg-red-500/10 border border-red-500/20 text-red-400 rounded-xl text-center font-bold text-xs">❌ Phone number mismatch. Authorization failed.</div>`;
            return;
        }

        // Render the order card in guest mode!
        const cardHtml = buildSingleOrderCardHtml(o, false, false, false, false, true); // isGuestMode = true
        resultDiv.innerHTML = `
            <div class="p-4 bg-teal-500/5 border border-teal-500/10 rounded-xl mb-4 text-xs text-teal-300 font-bold flex items-center gap-2">
                <i class="fa-solid fa-circle-check"></i> Request Authorized &amp; Loaded Successfully
            </div>
            ${cardHtml}
        `;
    } catch (err) {
        resultDiv.innerHTML = `<div class="p-4 bg-red-500/10 border border-red-500/20 text-red-400 rounded-xl text-center font-bold text-xs">❌ Search failed: ${err.message}</div>`;
    }
}

async function submitOrderReview(orderId, rating, reviewText) {
    if (!supabase) return;
    try {
        // Direct rating and review update
        const { error } = await supabase.from('orders').update({
            customer_rating: rating,
            customer_review: reviewText
        }).eq('id', orderId);

        if (error) {
            // Fallback: write to notes
            const { data } = await supabase.from('orders').select('notes').eq('id', orderId).single();
            const currentNotes = data?.notes || '';
            const updatedNotes = currentNotes + `\n[REVIEW] Rating: ${rating}/5, Comment: ${reviewText}`;
            await supabase.from('orders').update({ notes: updatedNotes }).eq('id', orderId);
        }
        showToast('⭐ Thank you for your feedback! Review saved successfully.', 'success');
        
        if (window.location.href.includes('dashboard.html')) {
            loadDashboard();
        } else {
            // Re-trigger guest tracking search to show the review instantly!
            const guestTrackForm = document.getElementById('guestTrackForm');
            if (guestTrackForm) {
                const submitEvent = new Event('submit');
                guestTrackForm.dispatchEvent(submitEvent);
            }
        }
    } catch (err) {
        showToast('Failed to save review: ' + err.message, 'error');
    }
}

// ─── 14. EXPOSE ATTACHMENTS FOR HTML HANDLERS ───
window.signInWithGoogle = signInWithGoogle;
window.toggleMobileMenu = toggleMobileMenu;
window.goToSlide = goToSlide;
window.nextSlide = nextSlide;
window.showToast = showToast;
window.loginUser = loginUser;
window.signupUser = signupUser;
window.loadDashboard = loadDashboard;
window.updateModels = updateModels;
window.updateRepairTypes = updateRepairTypes;
window.updatePartsSurvey = updatePartsSurvey;
window.createOrder = createOrder;
window.submitRequest = submitRequest;
window.updateReqModels = updateReqModels;
window.updateReqRepairTypes = updateReqRepairTypes;
window.toggleOther = toggleOther;
window.populateRequestBrands = populateRequestBrands;
window.assignOrderRoles = assignOrderRoles;
window.assignSelfAsTechnician = assignSelfAsTechnician;
window.assignSelfAsRepairMaster = assignSelfAsRepairMaster;
window.initiatePickup = initiatePickup;
window.verifyPickup = verifyPickup;
window.updateDiagnosis = updateDiagnosis;
window.requestAdditionalParts = requestAdditionalParts;
window.sendQuotation = sendQuotation;
window.confirmQuotation = confirmQuotation;
window.rejectQuotation = rejectQuotation;
window.updateProfile = updateProfile;
window.logoutUser = logoutUser;
window.completeRepair = completeRepair;
window.payForRepair = payForRepair;
window.executePayment = executePayment;
window.closeTicket = closeTicket;
window.trackOrderGuest = trackOrderGuest;
window.submitOrderReview = submitOrderReview;
