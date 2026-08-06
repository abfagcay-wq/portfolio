const About = () => {
  const faqs = [
    {
      question: "What is your specialization in IT?",
      answer: "I focus on Enterprise Resource Planning (ERP) and how systems can improve business processes."
    },
    {
      question: "Do you have work experience?",
      answer: "I have internship experience at DENR where I worked on organizing and documenting events."
    },
    {
      question: "What kind of projects do you work on?",
      answer: "I work on school projects, personal IT projects, and UI/UX design concepts."
    },
    {
      question: "Are you open to opportunities?",
      answer: "Yes, I am open to internships, part-time roles, and learning opportunities."
    },
    {
      question: "What are your career goals?",
      answer: "I aim to become a skilled IT professional specializing in ERP systems and software development."
    }
  ]

  return (
    <div className="pt-24 pb-20 px-4 min-h-screen bg-darker">
      <div className="max-w-4xl mx-auto">
        {/* About Me Section */}
        <section className="mb-16">
          <h1 className="text-4xl md:text-5xl font-bold mb-8 text-center bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            About Me
          </h1>
          <div className="bg-dark rounded-xl p-8 border border-gray-800">
            <p className="text-lg text-gray-300 leading-relaxed mb-4">
              My name is <span className="font-semibold text-white">Abraham Fagcayang</span>, and I am currently a student pursuing a Bachelor of Science in Information Technology with a focus on Enterprise Resource Planning (ERP). I am passionate about technology and how it can be used to improve systems and processes in organizations.
            </p>
            <p className="text-lg text-gray-300 leading-relaxed">
              I have gained practical experience through my internship at the Department of Environment and Natural Resources (DENR), where I helped organize and manage event-related data. This experience strengthened my attention to detail, communication skills, and ability to adapt in a professional environment.
            </p>
          </div>
        </section>

        {/* Skills Section */}
        <section className="mb-16">
          <h2 className="text-3xl font-bold mb-8 text-center text-white">Skills</h2>
          <div className="grid md:grid-cols-2 gap-6">
            <div className="bg-dark rounded-xl p-6 border border-gray-800">
              <h3 className="text-xl font-semibold mb-4 text-primary">Technical Skills</h3>
              <ul className="space-y-2 text-gray-300">
                <li className="flex items-center gap-2">
                  <span className="w-2 h-2 bg-primary rounded-full"></span>
                  Basic understanding of ERP systems
                </li>
                <li className="flex items-center gap-2">
                  <span className="w-2 h-2 bg-primary rounded-full"></span>
                  Web design fundamentals
                </li>
                <li className="flex items-center gap-2">
                  <span className="w-2 h-2 bg-primary rounded-full"></span>
                  Documentation and data organization
                </li>
              </ul>
            </div>
            <div className="bg-dark rounded-xl p-6 border border-gray-800">
              <h3 className="text-xl font-semibold mb-4 text-secondary">Soft Skills</h3>
              <ul className="space-y-2 text-gray-300">
                <li className="flex items-center gap-2">
                  <span className="w-2 h-2 bg-secondary rounded-full"></span>
                  Communication
                </li>
                <li className="flex items-center gap-2">
                  <span className="w-2 h-2 bg-secondary rounded-full"></span>
                  Adaptability
                </li>
                <li className="flex items-center gap-2">
                  <span className="w-2 h-2 bg-secondary rounded-full"></span>
                  Organization
                </li>
                <li className="flex items-center gap-2">
                  <span className="w-2 h-2 bg-secondary rounded-full"></span>
                  Teamwork
                </li>
              </ul>
            </div>
          </div>
        </section>

        {/* Education Section */}
        <section className="mb-16">
          <h2 className="text-3xl font-bold mb-8 text-center text-white">Education</h2>
          <div className="bg-dark rounded-xl p-6 border border-gray-800">
            <h3 className="text-xl font-semibold text-white mb-2">Bachelor of Science in Information Technology</h3>
            <p className="text-gray-400">Currently a student</p>
          </div>
        </section>

        {/* Contact Information */}
        <section className="mb-16">
          <h2 className="text-3xl font-bold mb-8 text-center text-white">Contact Information</h2>
          <div className="bg-dark rounded-xl p-6 border border-gray-800">
            <div className="space-y-4">
              <div className="flex items-center gap-4">
                <span className="text-2xl">📧</span>
                <div>
                  <p className="text-gray-400 text-sm">Email</p>
                  <a href="mailto:abfagcay@gmail.com" className="text-white hover:text-primary transition-colors">
                    abfagcay@gmail.com
                  </a>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <span className="text-2xl">📍</span>
                <div>
                  <p className="text-gray-400 text-sm">Location</p>
                  <p className="text-white">Baguio City, Philippines</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* FAQ Section */}
        <section className="mb-16">
          <h2 className="text-3xl font-bold mb-8 text-center text-white">Frequently Asked Questions</h2>
          <div className="space-y-4">
            {faqs.map((faq, index) => (
              <div key={index} className="bg-dark rounded-xl p-6 border border-gray-800">
                <h3 className="text-lg font-semibold text-white mb-2">Q: {faq.question}</h3>
                <p className="text-gray-400">A: {faq.answer}</p>
              </div>
            ))}
          </div>
        </section>

        {/* Contact Us Section */}
        <section className="text-center">
          <div className="bg-gradient-to-br from-primary/20 to-secondary/20 rounded-xl p-8 border border-primary/30">
            <h2 className="text-3xl font-bold mb-4 text-white">Contact Us</h2>
            <p className="text-gray-300 mb-6">
              Feel free to reach out for collaborations, opportunities, or questions.
            </p>
            <a 
              href="mailto:abfagcay@gmail.com" 
              className="inline-block px-8 py-4 bg-gradient-to-r from-primary to-secondary text-white font-semibold rounded-lg hover:opacity-90 transition-opacity shadow-lg shadow-primary/25"
            >
              Click here to contact me
            </a>
          </div>
        </section>
      </div>
    </div>
  )
}

export default About
