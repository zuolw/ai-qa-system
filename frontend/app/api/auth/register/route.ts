import { NextRequest } from 'next/server';

export async function POST(req: NextRequest) {
  const backendUrl = `http://localhost:8080/api/user/register`;

  try {
    const response = await fetch(backendUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(await req.json()),
    });

    const data = await response.json();

    // 后端返回 { token, status }，这里将token写入HttpOnly Cookie
    const headers: Record<string, string> = { 'Content-Type': 'application/json' };
    if (response.ok && data?.token) {
      const maxAge = 60 * 60 * 24; // 1 day
      headers['Set-Cookie'] = `token=${data.token}; Path=/; HttpOnly; SameSite=Lax; Max-Age=${maxAge}`;
    }

    return new Response(JSON.stringify(data), { status: response.status, headers });
  } catch (error) {
    return new Response(JSON.stringify({ error: '无法连接到后端服务' }), {
      status: 500,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }
}
