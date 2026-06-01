import Navbar from "./components/Navbar";
import HeroBanner from "./components/HeroBanner";
import ServiceCards from "./components/ServiceCards";
import Partners from "./components/Partners";
import FeaturedClinics from "./components/FeaturedClinics";
import DoctorConsultation from "./components/DoctorConsultation";
import HealthPackages from "./components/HealthPackages";
import Specializations from "./components/Specializations";
import Footer from "./components/Footer";

export default function Home() {
  return (
    <div className="min-h-screen bg-[var(--background)]">
      <Navbar />
      <HeroBanner />
      <ServiceCards />
      <Partners />
      <FeaturedClinics />
      <DoctorConsultation />
      <HealthPackages />
      <Specializations />
      <Footer />
    </div>
  );
}

