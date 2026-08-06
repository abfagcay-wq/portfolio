import { Link } from 'react-router-dom'

const Home = () => {
  return (
    <div className="pt-20">
      {/* Hero Section */}
      <section className="min-h-screen flex items-center justify-center bg-gradient-to-br from-darker via-dark to-darker px-4">
        <div className="max-w-4xl text-center">
          <div className="mb-8">
            <div className="w-32 h-32 mx-auto rounded-full bg-gradient-to-br from-primary to-secondary p-1">
              <div className="w-full h-full rounded-full bg-darker flex items-center justify-center">
                <span className="text-4xl font-bold text-white">AF</span>
              </div>
            </div>
          </div>
          <h1 className="text-5xl md:text-7xl font-bold mb-4 bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            Abraham Fagcayang
          </h1>
          <p className="text-xl md:text-2xl text-gray-300 mb-6">
            IT Student | Aspiring ERP Specialist
          </p>
          <p className="text-lg text-gray-400 max-w-2xl mx-auto mb-8 leading-relaxed">
            I am currently a student in Information Technology focusing on Enterprise Resource Planning (ERP), passionate about building efficient systems and solving real-world problems through technology. I enjoy organizing data, improving workflows, and continuously learning new tools to grow in the IT field.
          </p>
          <Link to="/gallery" className="inline-block px-8 py-4 bg-gradient-to-r from-primary to-secondary text-white font-semibold rounded-lg hover:opacity-90 transition-opacity shadow-lg shadow-primary/25">
            View My Work
          </Link>
        </div>
      </section>

      {/* About Preview */}
      <section className="py-20 px-4 bg-dark">
        <div className="max-w-4xl mx-auto">
          <h2 className="text-3xl md:text-4xl font-bold mb-8 text-center bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            About Preview
          </h2>
          <p className="text-lg text-gray-300 leading-relaxed text-center">
            I am currently a student with a strong interest in ERP systems and business solutions. I have gained hands-on experience during my internship at the Department of Environment and Natural Resources (DENR), where I developed skills in organization, communication, and adaptability.
          </p>
        </div>
      </section>

      {/* Key Skills */}
      <section className="py-20 px-4 bg-darker">
        <div className="max-w-4xl mx-auto">
          <h2 className="text-3xl md:text-4xl font-bold mb-12 text-center bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            Key Skills
          </h2>
          <div className="grid md:grid-cols-3 gap-6">
            <div className="bg-dark p-6 rounded-xl border border-gray-800 hover:border-primary/50 transition-colors">
              <div className="text-4xl mb-4">📋</div>
              <h3 className="text-xl font-semibold mb-2 text-white">Organized</h3>
              <p className="text-gray-400">Detail-oriented approach to tasks and projects</p>
            </div>
            <div className="bg-dark p-6 rounded-xl border border-gray-800 hover:border-primary/50 transition-colors">
              <div className="text-4xl mb-4">🔄</div>
              <h3 className="text-xl font-semibold mb-2 text-white">Adaptable</h3>
              <p className="text-gray-400">Quick to learn and adopt new technologies</p>
            </div>
            <div className="bg-dark p-6 rounded-xl border border-gray-800 hover:border-primary/50 transition-colors">
              <div className="text-4xl mb-4">💬</div>
              <h3 className="text-xl font-semibold mb-2 text-white">Communication</h3>
              <p className="text-gray-400">Effective verbal and written skills</p>
            </div>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="py-20 px-4 bg-gradient-to-br from-primary/20 to-secondary/20">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6 text-white">
            Interested in my work or want to collaborate?
          </h2>
          <p className="text-lg text-gray-300 mb-8">
            Explore my projects or reach out through the contact section.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/gallery" className="px-8 py-4 bg-gradient-to-r from-primary to-secondary text-white font-semibold rounded-lg hover:opacity-90 transition-opacity">
              Explore Projects
            </Link>
            <Link to="/about" className="px-8 py-4 bg-dark text-white font-semibold rounded-lg border border-gray-700 hover:border-primary transition-colors">
              Contact Me
            </Link>
          </div>
        </div>
      </section>
    </div>
  )
}

export default Home
