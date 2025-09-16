// app/api/chat/route.ts
import { google } from '@ai-sdk/google';
import { streamText } from 'ai';

// 配置正确的模型名称
const MODEL_NAME = 'gemini-1.0-pro'; // 或者使用 'gemini-pro'

// 模拟响应数据
const mockResponses: Record<string, string> = {
  '你好': '您好！我是AI助手，很高兴为您服务。',
  '你是谁': '我是基于Google Gemini AI构建的智能助手。',
  '介绍一下你自己': '我是由Google Gemini AI驱动的智能助手，可以帮助您回答问题、提供信息和进行有趣的对话。',
  'BFF是什么': 'BFF（Backend For Frontend）是一种架构模式，为前端应用提供定制化的API接口，位于前端和后端服务之间，可以解决跨域问题并聚合多个微服务的数据。',
  'Next.js': 'Next.js 是一个基于 React 的框架，提供服务器端渲染、静态站点生成、API路由等功能，非常适合构建现代Web应用。',
  '默认': '感谢您的消息！这是我作为AI助手的响应。'
};

export const runtime = 'edge';

export async function POST(req: Request) {
  // 检查API Key是否存在且有效
  const apiKey = process.env.GOOGLE_API_KEY;
  const hasValidApiKey = apiKey && apiKey.length > 30; // 简单的长度检查

  if (!hasValidApiKey) {
    console.log('使用模拟模式：GOOGLE_API_KEY未设置或无效');
    return createMockResponse(req);
  }

  try {
    const { messages } = await req.json();

    // 使用AI SDK Google提供者
    const result = await streamText({
      model: google(MODEL_NAME),
      messages: messages,
      system: '你是一个有帮助的AI助手。',
    });

    return result.toDataStreamResponse();
  } catch (error: any) {
    console.error('Gemini API调用失败:', error.message);

    // 如果是模型找不到的错误，尝试使用gemini-pro作为备选
    if (error.message.includes('not found') || error.message.includes('not supported')) {
      console.log('尝试使用 gemini-pro 模型');
      return tryWithFallbackModel(req, 'gemini-pro');
    }

    return createMockResponse(req);
  }
}

// 备选模型尝试
async function tryWithFallbackModel(req: Request, fallbackModel: string): Promise<Response> {
  try {
    const { messages } = await req.json();

    const result = await streamText({
      model: google(fallbackModel),
      messages: messages,
      system: '你是一个有帮助的AI助手。',
    });

    return result.toDataStreamResponse();
  } catch (fallbackError) {
    console.error('备选模型也失败:', fallbackError);
    return createMockResponse(req);
  }
}

// 创建模拟响应
async function createMockResponse(req: Request): Promise<Response> {
  const { messages } = await req.json();
  const lastMessage = messages[messages.length - 1]?.content || '你好';
  const responseText = mockResponses[lastMessage] || mockResponses['默认'];

  // 创建模拟的流式响应
  const encoder = new TextEncoder();
  const stream = new ReadableStream({
    async start(controller) {
      // 模拟流式输出效果
      const words = responseText.split(' ');
      for (const word of words) {
        await new Promise(resolve => setTimeout(resolve, 80));
        controller.enqueue(encoder.encode(word + ' '));
      }
      controller.close();
    }
  });

  return new Response(stream);
}
