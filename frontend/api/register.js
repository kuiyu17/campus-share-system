export default function handler(req, res) {
  if (req.method === 'POST') {
    const { username, password, name } = req.body;
    // 模拟注册逻辑
    if (username && password && name) {
      res.status(200).json({
        success: true,
        message: 'Registration successful'
      });
    } else {
      res.status(400).json({
        success: false,
        message: 'All fields are required'
      });
    }
  } else {
    res.status(405).json({
      success: false,
      message: 'Method not allowed'
    });
  }
}