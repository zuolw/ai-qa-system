import { NextRequest } from 'next/server';

export async function POST(req: NextRequest, { params }: { params: { slug: string[] } }) {
  const backendUrl = `http://localhost:8080/api/user/${params.slug.join('/')}`;
  
  try {
    const response = await fetch(backendUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(await req.json()),
    });

    const data = await response.json();
    
    // 处理cookie（如果后端设置了认证cookie）
    const cookies = response.headers.getSetCookie();
    if (cookies && cookies.length > 0) {
      return new Response(JSON.stringify(data), {
        status: response.status,
        headers: {
          'Content-Type': 'application/json',
          'Set-Cookie': cookies.join(', '),
        },
      });
    }

    return new Response(JSON.stringify(data), {
      status: response.status,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  } catch (error) {
    return new Response(JSON.stringify({ error: '无法连接到后端服务' }), {
      status: 500,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }
}