import ChatInterface from '../../components/ChatInterface';

export default function ChatPage() {
  return (
    <div className="min-h-screen gradient-bg">
      <div className="container mx-auto px-4 py-8 max-w-4xl">
        <header className="text-center mb-8">
          <h1 className="text-4xl font-bold text-white mb-2">AI聊天助手</h1>
          <p className="text-xl text-white opacity-90">基于Next.js + BFF架构 + Gemini AI</p>
        </header>
        
        <div className="bg-white rounded-2xl shadow-2xl overflow-hidden p-6">
          <ChatInterface />
        </div>
        
        <div className="mt-8 bg-white rounded-2xl shadow-2xl p-6">
          <h2 className="text-2xl font-bold mb-4 text-gray-800">系统架构说明</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
            <div className="bg-blue-50 p-4 rounded-lg">
              <h3 className="font-bold text-lg mb-2 text-blue-800">前端 (Next.js)</h3>
              <p className="text-gray-700">使用React和Next.js构建的用户界面，运行在端口3000上</p>
            </div>
            <div className="bg-purple-50 p-4 rounded-lg">
              <h3 className="font-bold text-lg mb-2 text-purple-800">BFF层 (Next.js API)</h3>
              <p className="text-gray-700">作为代理层，处理API请求转发，解决跨域问题</p>
            </div>
            <div className="bg-green-50 p-4 rounded-lg">
              <h3 className="font-bold text-lg mb-2 text-green-800">后端服务</h3>
              <p className="text-gray-700">Spring Cloud Gateway和微服务集群，运行在端口8080上</p>
            </div>
          </div>
          
          <div className="p-4 bg-gray-100 rounded-lg">
            <h4 className="font-bold mb-2 text-gray-800">BFF工作流程：</h4>
            <ol className="list-decimal pl-5 space-y-2 text-gray-700">
              <li>前端向<code className="bg-gray-200 px-1 rounded">/api/auth/login</code>发送登录请求（同源）</li>
              <li>BFF层接收请求并转发到<code className="bg-gray-200 px-1 rounded">http://localhost:8080/api/user/login</code></li>
              <li>后端服务处理请求并返回响应给BFF</li>
              <li>BFF将响应返回给前端，避免了浏览器跨域问题</li>
            </ol>
          </div>
        </div>
      </div>
    </div>
  );
}