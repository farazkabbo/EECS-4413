import { Link } from 'react-router-dom' 

export default function NotFoundPage() { 
  return ( 
    <div className="py-20 text-center"> 
      <h1 className="text-4xl font-bold">Page not found</h1> 
      <Link to="/" className="mt-4 inline-block text-brand hover:underline">Back to home</Link> 
    </div> 
  ) 
} 
