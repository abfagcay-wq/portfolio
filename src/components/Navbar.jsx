import { Link } from 'react-router-dom'

const Navbar = () => {
  return (
    <nav className="fixed top-0 left-0 right-0 bg-darker/90 backdrop-blur-md z-50 border-b border-gray-800">
      <div className="max-w-6xl mx-auto px-4 py-4">
        <div className="flex justify-between items-center">
          <Link to="/" className="text-2xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            AF
          </Link>
          <div className="flex gap-8">
            <Link to="/" className="text-gray-300 hover:text-white transition-colors">Home</Link>
            <Link to="/gallery" className="text-gray-300 hover:text-white transition-colors">Gallery</Link>
            <Link to="/about" className="text-gray-300 hover:text-white transition-colors">About</Link>
          </div>
        </div>
      </div>
    </nav>
  )
}

export default Navbar
