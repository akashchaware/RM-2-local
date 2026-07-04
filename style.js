// RepairMaster Web Design & Stylesheet Setup
// Configures Tailwind CSS and handles premium visual card effects

tailwind.config = {
    theme: {
        extend: {
            fontFamily: {
                sans: ['Inter', 'sans-serif'],
                display: ['Space Grotesk', 'sans-serif'],
            },
            colors: {
                navyBG: '#0A0F1D',
                navySurface: '#121B33',
                tealPrimary: '#0D9488', // Teal 600
                tealAccent: '#14B8A6', // Teal 500
                amberAccent: '#F59E0B', // Amber 500
                grayText: '#94A3B8', // Slate 400
                grayBorder: '#1E293B' // Slate 800
            }
        }
    }
};

// Apply interactive hover effects or ambient glows programmatically if needed
document.addEventListener('DOMContentLoaded', () => {
    console.log("RepairMaster Visual Styling Initialized");

    // Inject brand logo image into rm-logo elements
    const logoElements = document.querySelectorAll('.rm-logo');
    logoElements.forEach(el => {
        el.innerHTML = '';
        el.style.backgroundImage = "url('repo-image-folder/brand-logo-circular.jpg')";
        el.style.backgroundSize = "cover";
        el.style.backgroundPosition = "center";
        el.style.backgroundColor = "transparent";
        el.style.boxShadow = "0 8px 30px rgba(13, 148, 136, 0.4)";
        el.style.border = "1px solid rgba(20, 184, 166, 0.3)";
    });
});
