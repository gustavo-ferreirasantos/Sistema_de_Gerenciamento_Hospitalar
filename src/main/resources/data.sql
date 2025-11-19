INSERT INTO admin (email, nome,  password, cargo, nivel_acesso)
SELECT 'admin@gmail.com', 'admin', 'admin123', 'SUPER', 'SUPER'
    WHERE NOT EXISTS (SELECT 1 FROM admin WHERE email = 'admin@gmail.com');