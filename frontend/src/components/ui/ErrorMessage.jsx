export default function ErrorMessage({ error }) {
  if (!error) return null
  return (
    <div className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-700" role="alert">
      {error.message}
    </div>
  )
}
