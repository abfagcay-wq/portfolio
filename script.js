// Starfield Background Animation
function createStarfield() {
  const starfield = document.getElementById('starfield');
  if (!starfield) return;
  
  const starCount = 150;
  const shootingStarCount = 5;

  // Create twinkling stars
  for (let i = 0; i < starCount; i++) {
    const star = document.createElement('div');
    star.className = 'star';
    star.style.left = Math.random() * 100 + '%';
    star.style.top = Math.random() * 100 + '%';
    star.style.width = Math.random() * 3 + 1 + 'px';
    star.style.height = star.style.width;
    star.style.animationDelay = Math.random() * 3 + 's';
    star.style.animationDuration = (Math.random() * 2 + 2) + 's';
    starfield.appendChild(star);
  }

  // Create shooting stars
  for (let i = 0; i < shootingStarCount; i++) {
    const shootingStar = document.createElement('div');
    shootingStar.className = 'shooting-star';
    shootingStar.style.left = Math.random() * 100 + '%';
    shootingStar.style.top = Math.random() * 50 + '%';
    shootingStar.style.animationDelay = Math.random() * 10 + 's';
    shootingStar.style.animationDuration = (Math.random() * 3 + 2) + 's';
    starfield.appendChild(shootingStar);
  }
}

// Initialize starfield
createStarfield();

// Set active navigation item based on current page
function setActiveNavItem() {
  const currentPage = window.location.pathname.split('/').pop() || 'home.html';
  const navItems = document.querySelectorAll('.side-nav-item');
  
  navItems.forEach(item => {
    const itemPage = item.getAttribute('data-page');
    if (itemPage === currentPage.replace('.html', '')) {
      item.classList.add('active');
    } else {
      item.classList.remove('active');
    }
  });
}

// Initialize active state
setActiveNavItem();

// Scroll-triggered animations
const observerOptions = {
  threshold: 0.1,
  rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('visible');
    }
  });
}, observerOptions);

document.querySelectorAll('.section-dark, .section-darker').forEach(section => {
  observer.observe(section);
});
