const Gallery = () => {
  const projects = [
    {
      id: 1,
      title: "Event Documentation System (DENR Internship)",
      description: "During my internship at the Department of Environment and Natural Resources (DENR), I assisted in organizing and documenting official events attended by staff. This involved structuring records, maintaining accuracy, and ensuring accessibility of information.",
      icon: "📁"
    },
    {
      id: 2,
      title: "Student Portfolio Website",
      description: "A personal portfolio website designed to showcase my skills, projects, and background. This project focuses on clean UI/UX design, smooth navigation, and a modern visual style.",
      icon: "🌐"
    },
    {
      id: 3,
      title: "ERP Practice Project (Basic System Design)",
      description: "A conceptual ERP system created as part of my learning process. It demonstrates how business processes such as inventory and data management can be integrated into a single system.",
      icon: "⚙️"
    },
    {
      id: 4,
      title: "Academic Research: Chaw-es Ritual Study",
      description: "A research project focused on the traditional Chaw-es ritual in Tocucan, exploring its cultural significance and how it survives in modern times. This project enhanced my research, documentation, and analytical skills.",
      icon: "📚"
    },
    {
      id: 5,
      title: "UI/UX Design Concepts",
      description: "A collection of interface design ideas focusing on modern layouts, neon-themed visuals, and user-friendly navigation. These designs aim to improve user experience and visual engagement.",
      icon: "🎨"
    }
  ]

  return (
    <div className="pt-24 pb-20 px-4 min-h-screen bg-darker">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-4xl md:text-5xl font-bold mb-4 text-center bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
          My Works & Projects
        </h1>
        <p className="text-gray-400 text-center mb-12 max-w-2xl mx-auto">
          A collection of my academic projects, internship work, and design concepts showcasing my skills and growth in IT.
        </p>
        
        <div className="grid md:grid-cols-2 gap-8">
          {projects.map((project) => (
            <div 
              key={project.id} 
              className="bg-dark rounded-xl p-8 border border-gray-800 hover:border-primary/50 transition-all duration-300 hover:shadow-lg hover:shadow-primary/10"
            >
              <div className="text-5xl mb-6">{project.icon}</div>
              <h2 className="text-2xl font-bold mb-4 text-white">{project.title}</h2>
              <p className="text-gray-400 leading-relaxed">{project.description}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default Gallery
