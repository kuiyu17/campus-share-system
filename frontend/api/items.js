export default function handler(req, res) {
  // 模拟物品列表
  const items = [
    {
      id: 1,
      name: 'Textbook',
      category: 'Books',
      description: 'Introduction to Computer Science',
      owner: 'user1',
      status: 'available'
    },
    {
      id: 2,
      name: 'Laptop',
      category: 'Electronics',
      description: 'MacBook Pro 2022',
      owner: 'user2',
      status: 'available'
    }
  ];

  if (req.method === 'GET') {
    res.status(200).json({
      success: true,
      data: items
    });
  } else if (req.method === 'POST') {
    const newItem = req.body;
    items.push({
      id: items.length + 1,
      ...newItem,
      status: 'available'
    });
    res.status(200).json({
      success: true,
      message: 'Item created successfully'
    });
  } else {
    res.status(405).json({
      success: false,
      message: 'Method not allowed'
    });
  }
}