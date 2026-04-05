export default function handler(req, res) {
  if (req.method === 'POST') {
    const { username, phone, account, password } = req.body;
    // 处理前端发送的 account 字段
    const loginAccount = username || phone || account;
    // 模拟登录逻辑
    if (loginAccount && password) {
      res.status(200).json({
        id: 1,
        username: loginAccount,
        name: 'Test User',
        email: 'test@example.com',
        phone: phone || '13800138000'
      });
    } else {
      res.status(401).json({
        message: 'Invalid credentials'
      });
    }
  } else {
    res.status(405).json({
      message: 'Method not allowed'
    });
  }
}