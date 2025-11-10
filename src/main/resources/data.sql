INSERT INTO admin (email, password, cargo, nivel_acesso)
SELECT 'admin@a', '123456789', 'SUPER', 'SUPER'
    WHERE NOT EXISTS (SELECT 1 FROM admin WHERE email = 'admin@a');