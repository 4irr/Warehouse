const logRegBox = document.querySelector('.logreg-box');

const loginLink = document.querySelector('.login-link');

const registerLink = document.querySelector('.register-link');

registerLink.addEventListener('click', () => {
    logRegBox.classList.add('active');
});

loginLink.addEventListener('click', () => {
    logRegBox.classList.remove('active');
});

const isRegSuccess = document.querySelector('.form-box.register .isSuccess');
if (isRegSuccess!=null) {
    logRegBox.classList.add('active');
    document.querySelector('.form-box.register').scrollBy(0, 300);
}