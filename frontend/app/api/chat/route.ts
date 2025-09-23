// app/api/chat/route.ts
export const runtime = 'edge';

export async function POST(req: Request) {
  console.log("Chat route called");
  try {
    const { messages } = await req.json();
    console.log("Received messages:", messages);
    const lastMessage = messages[messages.length - 1]?.content || '';
    console.log("Last message content:", lastMessage);

    // 从请求Cookie中读取JWT
    const cookieHeader = req.headers.get('cookie') || '';
    const tokenMatch = cookieHeader.match(/(?:^|;\s*)token=([^;]+)/);
    const token = tokenMatch ? decodeURIComponent(tokenMatch[1]) : '';

    console.log("Extracted token:", token);

    console.log("Sending request to backend with question:", lastMessage);
    const res = await fetch('http://localhost:8080/api/qa/ask', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify({ question: lastMessage }),
    });

    const data = await res.json();
    console.log("Backend response data:", data);

    if (!res.ok) {
      console.log("Backend returned error status:", res.status);
      return new Response(JSON.stringify({ error: data.error || '请求失败' }), {
        status: res.status,
        headers: { 'Content-Type': 'application/json' },
      });
    }

    const answer = data?.answer || '抱歉，暂时无法回答。';
    console.log("Returning answer:", answer);
    return new Response(JSON.stringify({ answer }), {
      headers: { 'Content-Type': 'application/json' },
    });
  } catch (error) {
    console.log("Error in chat route:", error);
    return new Response(JSON.stringify({ error: '服务暂时不可用，请稍后再试。' }), {
      status: 500,
      headers: { 'Content-Type': 'application/json' },
    });
  }
}
