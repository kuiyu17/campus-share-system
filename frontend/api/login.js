export default function handler(req, res) {
  if (req.method === 'POST') {
    const { username, password } = req.body;
    // 模拟登录逻辑
    if (username && password) {
      res.status(200).json({
        success: true,
        token: 'mock-token',
        user: {
          id: 1,
          username: username,
          name: 'Test User'
        }
      });
    } else {
      res.status(401).json({
        success: false,
        message: 'Invalid username or password'
      });
    }
  } else {
    res.status(405).json({
      success: false,
      message: 'Method not allowed'
    });
  }
}