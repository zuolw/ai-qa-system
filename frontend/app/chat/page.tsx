import ChatInterface from '../../components/ChatInterface';

export default function ChatPage() {
  return (
    <div className="min-h-[calc(100vh-64px)]">
      <div className="mx-auto px-4 py-8 max-w-5xl">
        <header className="mb-6">
          <h1 className="text-3xl md:text-4xl font-bold text-white">AI 聊天助手</h1>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 glass-card rounded-3xl p-4 md:p-6">
            <ChatInterface />
          </div>
          <aside className="glass-card rounded-3xl p-4 md:p-6">
            <div className="text-white font-semibold mb-2">会话技巧</div>
            <ul className="text-white/70 text-sm space-y-2">
              <li>描述越具体，答案越准确</li>
              <li>使用上下文提升多轮对话效果</li>
              <li>可请求示例、步骤或代码片段</li>
            </ul>
            <div className="mt-6 text-white/50 text-xs">通过网关安全访问 QA Service</div>
          </aside>
        </div>
      </div>
    </div>
  );
}
