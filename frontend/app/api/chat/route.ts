// app/api/chat/route.ts
export const runtime = 'edge';

export async function POST(req: Request) {
  try {
    const { messages } = await req.json();
    const lastMessage = messages[messages.length - 1]?.content || '';

    // 从请求Cookie中读取JWT
    const cookieHeader = req.headers.get('cookie') || '';
    const tokenMatch = cookieHeader.match(/(?:^|;\s*)token=([^;]+)/);
    const token = tokenMatch ? decodeURIComponent(tokenMatch[1]) : '';

    const res = await fetch('http://localhost:8080/api/qa/ask', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify({ question: lastMessage }),
    });

    const data = await res.json();
    if (!res.ok) {
      return new Response(JSON.stringify(data), {
        status: res.status,
        headers: { 'Content-Type': 'application/json' },
      });
    }

    // 统一返回纯文本答案，前端 useChat 会以文本流/文本处理
    const answer = data?.answer || '抱歉，暂时无法回答。';
    return new Response(answer);
  } catch (error) {
    return new Response('服务暂时不可用，请稍后再试。');
  }
}
