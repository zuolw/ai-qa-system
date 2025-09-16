import AuthForm from '../../components/AuthForm';
import Link from 'next/link';

export default function RegisterPage() {
  return (
    <div className="min-h-screen flex items-center justify-center gradient-bg">
      <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-gray-800 text-center">创建新账户</h2>
        <AuthForm type="register" />
        <p className="text-center text-gray-600 mt-6">
          已有账户？{' '}
          <Link href="/login" className="text-blue-600 font-medium hover:underline">
            立即登录
          </Link>
        </p>
      </div>
    </div>
  );
}