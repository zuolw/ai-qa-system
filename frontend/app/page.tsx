// app/page.tsx
import Link from 'next/link';

export default function Home() {
  return (
    <div className="relative min-h-[calc(100vh-64px)]">
      <section className="mx-auto max-w-7xl px-6 pt-24 pb-16">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10 items-center">
          <div>
            <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/5 px-3 py-1 text-xs text-white/70 mb-6">
              <span className="h-2 w-2 rounded-full bg-emerald-400 animate-pulse"></span>
              实时 · AI 助手 · 微服务网关
            </div>
            <h1 className="text-4xl md:text-6xl font-bold leading-tight text-white">
              让你的团队，拥有<br className="hidden md:block" />
              更聪明的问答中枢
            </h1>
            <p className="mt-6 text-white/70 text-lg max-w-xl">
              基于 Next.js 与 Spring Cloud Gateway 的 AI 问答系统，集成用户认证、速率限制与多服务路由，提供安全高效的企业级体验。
            </p>
            <div className="mt-8 flex flex-wrap items-center gap-4">
              <Link href="/chat" className="inline-flex items-center rounded-xl bg-gradient-to-r from-cyan-400 to-fuchsia-500 text-white px-5 py-3 font-semibold shadow-lg shadow-fuchsia-500/20 hover:opacity-95 transition">
                立即体验聊天
              </Link>
              <Link href="/login" className="inline-flex items-center rounded-xl border border-white/20 text-white px-5 py-3 font-semibold hover:bg-white/10 transition">
                登录 / 注册
              </Link>
            </div>
          </div>
          <div>
            <div className="relative">
              <div className="absolute -inset-6 bg-gradient-to-tr from-cyan-400/20 to-fuchsia-500/20 blur-2xl rounded-3xl"></div>
              <div className="relative glass-card rounded-3xl p-6">
                <div className="grid grid-cols-2 gap-4">
                  <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <div className="text-white/60 text-sm">Nacos 注册中心</div>
                    <div className="mt-2 text-white font-semibold">服务发现与路由</div>
                  </div>
                  <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <div className="text-white/60 text-sm">Gateway</div>
                    <div className="mt-2 text-white font-semibold">认证 · 限流 · 转发</div>
                  </div>
                  <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <div className="text-white/60 text-sm">User Service</div>
                    <div className="mt-2 text-white font-semibold">JWT 登录与注册</div>
                  </div>
                  <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <div className="text-white/60 text-sm">QA Service</div>
                    <div className="mt-2 text-white font-semibold">Gemini 智能问答</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 pb-24">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Feature title="一键上手" desc="无需配置，开箱即用的全栈工程模板"/>
          <Feature title="企业级安全" desc="JWT、限流与服务鉴权全面覆盖"/>
          <Feature title="可观测性" desc="日志与链路轻松对接，问题可追踪"/>
        </div>
      </section>
    </div>
  );
}

function Feature({ title, desc }: { title: string; desc: string }) {
  return (
    <div className="glass-card rounded-2xl p-6">
      <div className="text-white font-semibold text-lg">{title}</div>
      <div className="text-white/70 mt-2 text-sm leading-relaxed">{desc}</div>
    </div>
  );
}