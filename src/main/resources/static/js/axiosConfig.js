// axiosConfig.js
import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8080'
});

instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

instance.interceptors.response.use(
    response => response,
    error => {
        if (error.response.status === 401) {
            // 토큰 만료 등의 이유로 인증 실패
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default instance;