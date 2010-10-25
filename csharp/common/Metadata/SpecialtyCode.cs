/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Collections.Generic;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// This is the code representing the clinical specialty of the clinician or provider who interacted with, treated, or
    /// provided a service to/for the patient
    /// </summary>
    public enum C80ClinicalSpecialties
    {
        /// <summary>
        /// C80 code for Nephrology (394589003)
        /// </summary>
        Nephrology,
        /// <summary>
        /// C80 code for Surgery-Dental-surgical-Prosthodontics (394606000)
        /// </summary>
        SurgeryDentalSurgicalProsthodontics,
        /// <summary>
        /// C80 code for Histopathology (394597005)
        /// </summary>
        Histopathology,
        /// <summary>
        /// C80 code for Gynecology (394586005)
        /// </summary>
        Gynecology,
        /// <summary>
        /// C80 code for Infectious diseases (394807007)
        /// </summary>
        InfectiousDiseases,
        /// <summary>
        /// C80 code for Burns care (408462000)
        /// </summary>
        BurnsCare,
        /// <summary>
        /// C80 code for Pediatric gastroenterology (418058008)
        /// </summary>
        PediatricGastroenterology,
        /// <summary>
        /// C80 code for Learning disability (408468001)
        /// </summary>
        LearningDisability,
        /// <summary>
        /// C80 code for Clinical genetics (394580004)
        /// </summary>
        ClinicalGenetics,
        /// <summary>
        /// C80 code for Occupational medicine (394821009)
        /// </summary>
        OccupationalMedicine,
        /// <summary>
        /// C80 code for Neurology (394591006)
        /// </summary>
        Neurology,
        /// <summary>
        /// C80 code for Audiological medicine (394578005)
        /// </summary>
        AudiologicalMedicine,
        /// <summary>
        /// C80 code for Surgery-Vascular (394611003)
        /// </summary>
        SurgeryVascular,
        /// <summary>
        /// C80 code for Surgery-Trauma and orthopedics (408463005)
        /// </summary>
        SurgeryTraumaAndOrthopedics,
        /// <summary>
        /// C80 code for Obstetrics and gynecology (394585009)
        /// </summary>
        ObstetricsAndGynecology,
        /// <summary>
        /// C80 code for Surgery-general (408474001)
        /// </summary>
        SurgeryGeneral,
        /// <summary>
        /// C80 code for Medical specialty-Other (394733009)
        /// </summary>
        MedicalSpecialtyOther,
        /// <summary>
        /// C80 code for Surgery-Dermatologic surgery (394604002)
        /// </summary>
        SurgeryDermatologicSurgery,
        /// <summary>
        /// C80 code for Pediatric rheumatology (419472004)
        /// </summary>
        PediatricRheumatology,
        /// <summary>
        /// C80 code for Medical ophthalmology (394813003)
        /// </summary>
        MedicalOphthalmology,
        /// <summary>
        /// C80 code for Urology (394612005)
        /// </summary>
        Urology,
        /// <summary>
        /// C80 code for Obstetrics (408470005)
        /// </summary>
        Obstetrics,
        /// <summary>
        /// C80 code for Clinical pharmacology (394600006)
        /// </summary>
        ClinicalPharmacology,
        /// <summary>
        /// C80 code for Gastroenterology (394584008)
        /// </summary>
        Gastroenterology,
        /// <summary>
        /// C80 code for Pediatric oncology (418002000)
        /// </summary>
        PediatricOncology,
        /// <summary>
        /// C80 code for Pediatric surgery-bone marrow transplantation (420112009)
        /// </summary>
        PediatricSurgeryBoneMarrowTransplantation,
        /// <summary>
        /// C80 code for Palliative medicine (394806003)
        /// </summary>
        PalliativeMedicine,
        /// <summary>
        /// C80 code for Surgery-Dentistry-Restorative dentistry (408449004)
        /// </summary>
        SurgeryDentistryRestorativeDentistry,
        /// <summary>
        /// C80 code for Blood banking and transfusion medicine (421661004)
        /// </summary>
        BloodBankingAndTransfusionMedicine,
        /// <summary>
        /// C80 code for Surgical specialty-Other (394732004)
        /// </summary>
        SurgicalSpecialtyOther,
        /// <summary>
        /// C80 code for Anesthetics (394577000)
        /// </summary>
        Anesthetics,
        /// <summary>
        /// C80 code for Radiation oncology (419815003)
        /// </summary>
        RadiationOncology,
        /// <summary>
        /// C80 code for Osteopathic manipulative medicine (416304004)
        /// </summary>
        OsteopathicManipulativeMedicine,
        /// <summary>
        /// C80 code for Surgical oncology (419321007)
        /// </summary>
        SurgicalOncology,
        /// <summary>
        /// C80 code for Preventive medicine (409968004)
        /// </summary>
        PreventiveMedicine,
        /// <summary>
        /// C80 code for Surgery-Hepatobiliary and pancreatic surgery (394610002)
        /// </summary>
        SurgeryHepatobiliaryAndPancreaticSurgery,
        /// <summary>
        /// C80 code for Surgery-Dental-Oral and maxillofacial surgery (408466002)
        /// </summary>
        SurgeryDentalOralAndMaxillofacialSurgery,
        /// <summary>
        /// C80 code for Endocrinology (394583002)
        /// </summary>
        Endocrinology,
        /// <summary>
        /// C80 code for Thoracic medicine (394590007)
        /// </summary>
        ThoracicMedicine,
        /// <summary>
        /// C80 code for Hematopathology (394916005)
        /// </summary>
        Hematopathology,
        /// <summary>
        /// C80 code for Sleep studies (408450004)
        /// </summary>
        SleepStudies,
        /// <summary>
        /// C80 code for Hepatology (408472002)
        /// </summary>
        Hepatology,
        /// <summary>
        /// C80 code for Pediatric nephrology (419365004)
        /// </summary>
        PediatricNephrology,
        /// <summary>
        /// C80 code for Critical care medicine (408478003)
        /// </summary>
        CriticalCareMedicine,
        /// <summary>
        /// C80 code for Pediatric (Child and adolescent) psychiatry (394588006)
        /// </summary>
        PediatricChildAndAdolescentPsychiatry,
        /// <summary>
        /// C80 code for Dental medicine specialties (394812008)
        /// </summary>
        DentalMedicineSpecialties,
        /// <summary>
        /// C80 code for General medical practice (408443003)
        /// </summary>
        GeneralMedicalPractice,
        /// <summary>
        /// C80 code for Radiology-Interventional radiology (408455009)
        /// </summary>
        RadiologyInterventionalRadiology,
        /// <summary>
        /// C80 code for Surgery-Transplantation surgery (394801008)
        /// </summary>
        SurgeryTransplantationSurgery,
        /// <summary>
        /// C80 code for General pathology (394915009)
        /// </summary>
        GeneralPathology,
        /// <summary>
        /// C80 code for Tropical medicine (408448007)
        /// </summary>
        TropicalMedicine,
        /// <summary>
        /// C80 code for Nuclear medicine (394649004)
        /// </summary>
        NuclearMedicine,
        /// <summary>
        /// C80 code for Pediatric infectious diseases (418862001)
        /// </summary>
        PediatricInfectiousDiseases,
        /// <summary>
        /// C80 code for Urological oncology (419043006)
        /// </summary>
        UrologicalOncology,
        /// <summary>
        /// C80 code for Surgery-Dentistry-surgical (394608004)
        /// </summary>
        SurgeryDentistrySurgical,
        /// <summary>
        /// C80 code for Pediatric cardiology (408459003)
        /// </summary>
        PediatricCardiology,
        /// <summary>
        /// C80 code for Dental-General dental practice (408444009)
        /// </summary>
        DentalGeneralDentalPractice,
        /// <summary>
        /// C80 code for Rheumatology (394810000)
        /// </summary>
        Rheumatology,
        /// <summary>
        /// C80 code for Pediatric ophthalmology (419983000)
        /// </summary>
        PediatricOphthalmology,
        /// <summary>
        /// C80 code for Surgery-Dental-Endodontics (408465003)
        /// </summary>
        SurgeryDentalEndodontics,
        /// <summary>
        /// C80 code for Immunopathology (394598000)
        /// </summary>
        Immunopathology,
        /// <summary>
        /// C80 code for Pulmonary medicine (418112009)
        /// </summary>
        PulmonaryMedicine,
        /// <summary>
        /// C80 code for Adult mental illness (408467006)
        /// </summary>
        AdultMentalIllness,
        /// <summary>
        /// C80 code for Ophthalmology (394594003)
        /// </summary>
        Ophthalmology,
        /// <summary>
        /// C80 code for Clinical immunology (408480009)
        /// </summary>
        ClinicalImmunology,
        /// <summary>
        /// C80 code for Pediatric pulmonology (419170002)
        /// </summary>
        PediatricPulmonology,
        /// <summary>
        /// C80 code for Surgery-Neurosurgery (394609007)
        /// </summary>
        SurgeryNeurosurgery,
        /// <summary>
        /// C80 code for Community medicine (394581000)
        /// </summary>
        CommunityMedicine,
        /// <summary>
        /// C80 code for Toxicology (409967009)
        /// </summary>
        Toxicology,
        /// <summary>
        /// C80 code for Geriatric medicine (394811001)
        /// </summary>
        GeriatricMedicine,
        /// <summary>
        /// C80 code for Psychiatry (394587001)
        /// </summary>
        Psychiatry,
        /// <summary>
        /// C80 code for Neuropathology (394599008)
        /// </summary>
        Neuropathology,
        /// <summary>
        /// C80 code for Surgery-Dental-Orthodontics (408461007)
        /// </summary>
        SurgeryDentalOrthodontics,
        /// <summary>
        /// C80 code for Surgery-Dentistry-surgical-Orthodontics (418018006)
        /// </summary>
        SurgeryDentistrySurgicalOrthodontics,
        /// <summary>
        /// C80 code for Dermatology (394582007)
        /// </summary>
        Dermatology,
        /// <summary>
        /// C80 code for Clinical cytogenetics and molecular genetics (394804000)
        /// </summary>
        ClinicalCytogeneticsAndMolecularGenetics,
        /// <summary>
        /// C80 code for Pediatric genetics (420208008)
        /// </summary>
        PediatricGenetics,
        /// <summary>
        /// C80 code for Respite care (408447002)
        /// </summary>
        RespiteCare,
        /// <summary>
        /// C80 code for Surgical-Accident and emergency (394576009)
        /// </summary>
        SurgicalAccidentEmergency,
        /// <summary>
        /// C80 code for Clinical oncology (394592004)
        /// </summary>
        ClinicalOncology,
        /// <summary>
        /// C80 code for Surgery-Colorectal surgery (408441001)
        /// </summary>
        SurgeryColorectalSurgery,
        /// <summary>
        /// C80 code for Ophthalmic surgery (422191005)
        /// </summary>
        OphthalmicSurgery,
        /// <summary>
        /// C80 code for Clinical neuro-physiology (394809005)
        /// </summary>
        ClinicalNeuroPhysiology,
        /// <summary>
        /// C80 code for Surgery-Plastic surgery (408477008)
        /// </summary>
        SurgeryPlasticSurgery,
        /// <summary>
        /// C80 code for Surgery-Bone and marrow transplantation (408476004)
        /// </summary>
        SurgeryBoneAndMarrowTransplantation,
        /// <summary>
        /// C80 code for Clinical microbiology (408454008)
        /// </summary>
        ClinicalMicrobiology,
        /// <summary>
        /// C80 code for Internal medicine (419192003)
        /// </summary>
        InternalMedicine,
        /// <summary>
        /// C80 code for Dive medicine (410005002)
        /// </summary>
        DiveMedicine,
        /// <summary>
        /// C80 code for Radiology (394914008)
        /// </summary>
        Radiology,
        /// <summary>
        /// C80 code for Otolaryngology (418960008)
        /// </summary>
        Otolaryngology,
        /// <summary>
        /// C80 code for Pain management (394882004)
        /// </summary>
        PainManagement,
        /// <summary>
        /// C80 code for Clinical hematology (394803006)
        /// </summary>
        ClinicalHematology,
        /// <summary>
        /// C80 code for Surgery-Dental-Prosthetic dentistry (Prosthodontics) (408460008)
        /// </summary>
        SurgeryDentalProstheticDentistryProsthodontics,
        /// <summary>
        /// C80 code for Military medicine (410001006)
        /// </summary>
        MilitaryMedicine,
        /// <summary>
        /// C80 code for Rehabilitation (394602003)
        /// </summary>
        Rehabilitation,
        /// <summary>
        /// C80 code for Clinical physiology (394601005)
        /// </summary>
        ClinicalPhysiology,
        /// <summary>
        /// C80 code for Pediatric surgery (394539006)
        /// </summary>
        PediatricSurgery,
        /// <summary>
        /// C80 code for Medical oncology (394593009)
        /// </summary>
        MedicalOncology,
        /// <summary>
        /// C80 code for Pediatric immunology (418535003)
        /// </summary>
        PediatricImmunology,
        /// <summary>
        /// C80 code for Cardiology (394579002)
        /// </summary>
        Cardiology,
        /// <summary>
        /// C80 code for Pediatric endocrinology (419610006)
        /// </summary>
        PediatricEndocrinology,
        /// <summary>
        /// C80 code for Pediatric dentistry (394607009)
        /// </summary>
        PediatricDentistry,
        /// <summary>
        /// C80 code for Psychotherapy (394913002)
        /// </summary>
        Psychotherapy,
        /// <summary>
        /// C80 code for Pediatric hematology (418652005)
        /// </summary>
        PediatricHematology,
        /// <summary>
        /// C80 code for Surgery-Cardiothoracic transplantation (408464004)
        /// </summary>
        SurgeryCardiothoracicTransplantation,
        /// <summary>
        /// C80 code for Gynecological oncology (408446006)
        /// </summary>
        GynecologicalOncology,
        /// <summary>
        /// C80 code for Diabetic medicine (408475000)
        /// </summary>
        DiabeticMedicine,
        /// <summary>
        /// C80 code for Surgery-Cardiac surgery (408471009)
        /// </summary>
        SurgeryCardiacSurgery,
        /// <summary>
        /// C80 code for Public health medicine (408440000)
        /// </summary>
        PublicHealthMedicine,
        /// <summary>
        /// C80 code for Genito-urinary medicine (394808002)
        /// </summary>
        GenitoUrinaryMedicine,
        /// <summary>
        /// C80 code for Surgery-Breast surgery (408469009)
        /// </summary>
        SurgeryBreastSurgery,
        /// <summary>
        /// C80 code for Family practice (419772000)
        /// </summary>
        FamilyPractice,
        /// <summary>
        /// C80 code for General practice (394814009)
        /// </summary>
        GeneralPractice,
        /// <summary>
        /// C80 code for General medicine (394802001)
        /// </summary>
        GeneralMedicine,
        /// <summary>
        /// C80 code for Surgery-Ear (394605001)
        /// </summary>
        SurgeryEar
    }

    /// <summary>
    /// Represents a coded clinical specialty.
    /// </summary>
    public static class C80SpecialtyCodeUtils
    {

        /// <summary>
        /// Returns a <see cref="CodedValue"/> for the code
        /// </summary>
        public static CodedValue ToCodedValue(this C80ClinicalSpecialties code)
        {
            KeyValuePair<string, string> pair = Decode(code);
            return new CodedValue(pair.Key, pair.Value, "2.16.840.1.113883.3.88.12.80.72");
        }

        private static Dictionary<C80ClinicalSpecialties, KeyValuePair<string, string>> m_C80ClinicalSpecialties
            = new Dictionary<C80ClinicalSpecialties, KeyValuePair<string, string>>()
                  {
                      {C80ClinicalSpecialties.Nephrology, new KeyValuePair<string, string>("394589003", "Nephrology")},
                      {
                          C80ClinicalSpecialties.SurgeryDentalSurgicalProsthodontics,
                          new KeyValuePair<string, string>("394606000", "Surgery-Dental-surgical-Prosthodontics")
                          },
                      {
                          C80ClinicalSpecialties.Histopathology,
                          new KeyValuePair<string, string>("394597005", "Histopathology")
                          },
                      {C80ClinicalSpecialties.Gynecology, new KeyValuePair<string, string>("394586005", "Gynecology")},
                      {
                          C80ClinicalSpecialties.InfectiousDiseases,
                          new KeyValuePair<string, string>("394807007", "Infectious diseases")
                          },
                      {C80ClinicalSpecialties.BurnsCare, new KeyValuePair<string, string>("408462000", "Burns care")},
                      {
                          C80ClinicalSpecialties.PediatricGastroenterology,
                          new KeyValuePair<string, string>("418058008", "Pediatric gastroenterology")
                          },
                      {
                          C80ClinicalSpecialties.LearningDisability,
                          new KeyValuePair<string, string>("408468001", "Learning disability")
                          },
                      {
                          C80ClinicalSpecialties.ClinicalGenetics,
                          new KeyValuePair<string, string>("394580004", "Clinical genetics")
                          },
                      {
                          C80ClinicalSpecialties.OccupationalMedicine,
                          new KeyValuePair<string, string>("394821009", "Occupational medicine")
                          },
                      {C80ClinicalSpecialties.Neurology, new KeyValuePair<string, string>("394591006", "Neurology")},
                      {
                          C80ClinicalSpecialties.AudiologicalMedicine,
                          new KeyValuePair<string, string>("394578005", "Audiological medicine")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryVascular,
                          new KeyValuePair<string, string>("394611003", "Surgery-Vascular")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryTraumaAndOrthopedics,
                          new KeyValuePair<string, string>("408463005", "Surgery-Trauma and orthopedics")
                          },
                      {
                          C80ClinicalSpecialties.ObstetricsAndGynecology,
                          new KeyValuePair<string, string>("394585009", "Obstetrics and gynecology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryGeneral,
                          new KeyValuePair<string, string>("408474001", "Surgery-general")
                          },
                      {
                          C80ClinicalSpecialties.MedicalSpecialtyOther,
                          new KeyValuePair<string, string>("394733009", "Medical specialty-Other")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryDermatologicSurgery,
                          new KeyValuePair<string, string>("394604002", "Surgery-Dermatologic surgery")
                          },
                      {
                          C80ClinicalSpecialties.PediatricRheumatology,
                          new KeyValuePair<string, string>("419472004", "Pediatric rheumatology")
                          },
                      {
                          C80ClinicalSpecialties.MedicalOphthalmology,
                          new KeyValuePair<string, string>("394813003", "Medical ophthalmology")
                          },
                      {C80ClinicalSpecialties.Urology, new KeyValuePair<string, string>("394612005", "Urology")},
                      {C80ClinicalSpecialties.Obstetrics, new KeyValuePair<string, string>("408470005", "Obstetrics")},
                      {
                          C80ClinicalSpecialties.ClinicalPharmacology,
                          new KeyValuePair<string, string>("394600006", "Clinical pharmacology")
                          },
                      {
                          C80ClinicalSpecialties.Gastroenterology,
                          new KeyValuePair<string, string>("394584008", "Gastroenterology")
                          },
                      {
                          C80ClinicalSpecialties.PediatricOncology,
                          new KeyValuePair<string, string>("418002000", "Pediatric oncology")
                          },
                      {
                          C80ClinicalSpecialties.PediatricSurgeryBoneMarrowTransplantation,
                          new KeyValuePair<string, string>("420112009", "Pediatric surgery-bone marrow transplantation")
                          },
                      {
                          C80ClinicalSpecialties.PalliativeMedicine,
                          new KeyValuePair<string, string>("394806003", "Palliative medicine")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryDentistryRestorativeDentistry,
                          new KeyValuePair<string, string>("408449004", "Surgery-Dentistry-Restorative dentistry")
                          },
                      {
                          C80ClinicalSpecialties.BloodBankingAndTransfusionMedicine,
                          new KeyValuePair<string, string>("421661004", "Blood banking and transfusion medicine")
                          },
                      {
                          C80ClinicalSpecialties.SurgicalSpecialtyOther,
                          new KeyValuePair<string, string>("394732004", "Surgical specialty-Other")
                          },
                      {C80ClinicalSpecialties.Anesthetics, new KeyValuePair<string, string>("394577000", "Anesthetics")},
                      {
                          C80ClinicalSpecialties.RadiationOncology,
                          new KeyValuePair<string, string>("419815003", "Radiation oncology")
                          },
                      {
                          C80ClinicalSpecialties.OsteopathicManipulativeMedicine,
                          new KeyValuePair<string, string>("416304004", "Osteopathic manipulative medicine")
                          },
                      {
                          C80ClinicalSpecialties.SurgicalOncology,
                          new KeyValuePair<string, string>("419321007", "Surgical oncology")
                          },
                      {
                          C80ClinicalSpecialties.PreventiveMedicine,
                          new KeyValuePair<string, string>("409968004", "Preventive medicine")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryHepatobiliaryAndPancreaticSurgery,
                          new KeyValuePair<string, string>("394610002", "Surgery-Hepatobiliary and pancreatic surgery")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryDentalOralAndMaxillofacialSurgery,
                          new KeyValuePair<string, string>("408466002", "Surgery-Dental-Oral and maxillofacial surgery")
                          },
                      {
                          C80ClinicalSpecialties.Endocrinology,
                          new KeyValuePair<string, string>("394583002", "Endocrinology")
                          },
                      {
                          C80ClinicalSpecialties.ThoracicMedicine,
                          new KeyValuePair<string, string>("394590007", "Thoracic medicine")
                          },
                      {
                          C80ClinicalSpecialties.Hematopathology,
                          new KeyValuePair<string, string>("394916005", "Hematopathology")
                          },
                      {
                          C80ClinicalSpecialties.SleepStudies,
                          new KeyValuePair<string, string>("408450004", "Sleep studies")
                          },
                      {C80ClinicalSpecialties.Hepatology, new KeyValuePair<string, string>("408472002", "Hepatology")},
                      {
                          C80ClinicalSpecialties.PediatricNephrology,
                          new KeyValuePair<string, string>("419365004", "Pediatric nephrology")
                          },
                      {
                          C80ClinicalSpecialties.CriticalCareMedicine,
                          new KeyValuePair<string, string>("408478003", "Critical care medicine")
                          },
                      {
                          C80ClinicalSpecialties.PediatricChildAndAdolescentPsychiatry,
                          new KeyValuePair<string, string>("394588006", "Pediatric (Child and adolescent) psychiatry")
                          },
                      {
                          C80ClinicalSpecialties.DentalMedicineSpecialties,
                          new KeyValuePair<string, string>("394812008", "Dental medicine specialties")
                          },
                      {
                          C80ClinicalSpecialties.GeneralMedicalPractice,
                          new KeyValuePair<string, string>("408443003", "General medical practice")
                          },
                      {
                          C80ClinicalSpecialties.RadiologyInterventionalRadiology,
                          new KeyValuePair<string, string>("408455009", "Radiology-Interventional radiology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryTransplantationSurgery,
                          new KeyValuePair<string, string>("394801008", "Surgery-Transplantation surgery")
                          },
                      {
                          C80ClinicalSpecialties.GeneralPathology,
                          new KeyValuePair<string, string>("394915009", "General pathology")
                          },
                      {
                          C80ClinicalSpecialties.TropicalMedicine,
                          new KeyValuePair<string, string>("408448007", "Tropical medicine")
                          },
                      {
                          C80ClinicalSpecialties.NuclearMedicine,
                          new KeyValuePair<string, string>("394649004", "Nuclear medicine")
                          },
                      {
                          C80ClinicalSpecialties.PediatricInfectiousDiseases,
                          new KeyValuePair<string, string>("418862001", "Pediatric infectious diseases")
                          },
                      {
                          C80ClinicalSpecialties.UrologicalOncology,
                          new KeyValuePair<string, string>("419043006", "Urological oncology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryDentistrySurgical,
                          new KeyValuePair<string, string>("394608004", "Surgery-Dentistry-surgical")
                          },
                      {
                          C80ClinicalSpecialties.PediatricCardiology,
                          new KeyValuePair<string, string>("408459003", "Pediatric cardiology")
                          },
                      {
                          C80ClinicalSpecialties.DentalGeneralDentalPractice,
                          new KeyValuePair<string, string>("408444009", "Dental-General dental practice")
                          },
                      {
                          C80ClinicalSpecialties.Rheumatology,
                          new KeyValuePair<string, string>("394810000", "Rheumatology")
                          },
                      {
                          C80ClinicalSpecialties.PediatricOphthalmology,
                          new KeyValuePair<string, string>("419983000", "Pediatric ophthalmology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryDentalEndodontics,
                          new KeyValuePair<string, string>("408465003", "Surgery-Dental-Endodontics")
                          },
                      {
                          C80ClinicalSpecialties.Immunopathology,
                          new KeyValuePair<string, string>("394598000", "Immunopathology")
                          },
                      {
                          C80ClinicalSpecialties.PulmonaryMedicine,
                          new KeyValuePair<string, string>("418112009", "Pulmonary medicine")
                          },
                      {
                          C80ClinicalSpecialties.AdultMentalIllness,
                          new KeyValuePair<string, string>("408467006", "Adult mental illness")
                          },
                      {
                          C80ClinicalSpecialties.Ophthalmology,
                          new KeyValuePair<string, string>("394594003", "Ophthalmology")
                          },
                      {
                          C80ClinicalSpecialties.ClinicalImmunology,
                          new KeyValuePair<string, string>("408480009", "Clinical immunology")
                          },
                      {
                          C80ClinicalSpecialties.PediatricPulmonology,
                          new KeyValuePair<string, string>("419170002", "Pediatric pulmonology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryNeurosurgery,
                          new KeyValuePair<string, string>("394609007", "Surgery-Neurosurgery")
                          },
                      {
                          C80ClinicalSpecialties.CommunityMedicine,
                          new KeyValuePair<string, string>("394581000", "Community medicine")
                          },
                      {C80ClinicalSpecialties.Toxicology, new KeyValuePair<string, string>("409967009", "Toxicology")},
                      {
                          C80ClinicalSpecialties.GeriatricMedicine,
                          new KeyValuePair<string, string>("394811001", "Geriatric medicine")
                          },
                      {C80ClinicalSpecialties.Psychiatry, new KeyValuePair<string, string>("394587001", "Psychiatry")},
                      {
                          C80ClinicalSpecialties.Neuropathology,
                          new KeyValuePair<string, string>("394599008", "Neuropathology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryDentalOrthodontics,
                          new KeyValuePair<string, string>("408461007", "Surgery-Dental-Orthodontics")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryDentistrySurgicalOrthodontics,
                          new KeyValuePair<string, string>("418018006", "Surgery-Dentistry-surgical-Orthodontics")
                          },
                      {C80ClinicalSpecialties.Dermatology, new KeyValuePair<string, string>("394582007", "Dermatology")},
                      {
                          C80ClinicalSpecialties.ClinicalCytogeneticsAndMolecularGenetics,
                          new KeyValuePair<string, string>("394804000", "Clinical cytogenetics and molecular genetics")
                          },
                      {
                          C80ClinicalSpecialties.PediatricGenetics,
                          new KeyValuePair<string, string>("420208008", "Pediatric genetics")
                          },
                      {
                          C80ClinicalSpecialties.RespiteCare, new KeyValuePair<string, string>("408447002", "Respite care")
                          },
                      {
                          C80ClinicalSpecialties.SurgicalAccidentEmergency,
                          new KeyValuePair<string, string>("394576009", "Surgical-Accident & emergency")
                          },
                      {
                          C80ClinicalSpecialties.ClinicalOncology,
                          new KeyValuePair<string, string>("394592004", "Clinical oncology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryColorectalSurgery,
                          new KeyValuePair<string, string>("408441001", "Surgery-Colorectal surgery")
                          },
                      {
                          C80ClinicalSpecialties.OphthalmicSurgery,
                          new KeyValuePair<string, string>("422191005", "Ophthalmic surgery")
                          },
                      {
                          C80ClinicalSpecialties.ClinicalNeuroPhysiology,
                          new KeyValuePair<string, string>("394809005", "Clinical neuro-physiology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryPlasticSurgery,
                          new KeyValuePair<string, string>("408477008", "Surgery-Plastic surgery")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryBoneAndMarrowTransplantation,
                          new KeyValuePair<string, string>("408476004", "Surgery-Bone and marrow transplantation")
                          },
                      {
                          C80ClinicalSpecialties.ClinicalMicrobiology,
                          new KeyValuePair<string, string>("408454008", "Clinical microbiology")
                          },
                      {
                          C80ClinicalSpecialties.InternalMedicine,
                          new KeyValuePair<string, string>("419192003", "Internal medicine")
                          },
                      {
                          C80ClinicalSpecialties.DiveMedicine,
                          new KeyValuePair<string, string>("410005002", "Dive medicine")
                          },
                      {C80ClinicalSpecialties.Radiology, new KeyValuePair<string, string>("394914008", "Radiology")},
                      {
                          C80ClinicalSpecialties.Otolaryngology,
                          new KeyValuePair<string, string>("418960008", "Otolaryngology")
                          },
                      {
                          C80ClinicalSpecialties.PainManagement,
                          new KeyValuePair<string, string>("394882004", "Pain management")
                          },
                      {
                          C80ClinicalSpecialties.ClinicalHematology,
                          new KeyValuePair<string, string>("394803006", "Clinical hematology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryDentalProstheticDentistryProsthodontics,
                          new KeyValuePair<string, string>("408460008",
                                                           "Surgery-Dental-Prosthetic dentistry (Prosthodontics)")
                          },
                      {
                          C80ClinicalSpecialties.MilitaryMedicine,
                          new KeyValuePair<string, string>("410001006", "Military medicine")
                          },
                      {
                          C80ClinicalSpecialties.Rehabilitation,
                          new KeyValuePair<string, string>("394602003", "Rehabilitation")
                          },
                      {
                          C80ClinicalSpecialties.ClinicalPhysiology,
                          new KeyValuePair<string, string>("394601005", "Clinical physiology")
                          },
                      {
                          C80ClinicalSpecialties.PediatricSurgery,
                          new KeyValuePair<string, string>("394539006", "Pediatric surgery")
                          },
                      {
                          C80ClinicalSpecialties.MedicalOncology,
                          new KeyValuePair<string, string>("394593009", "Medical oncology")
                          },
                      {
                          C80ClinicalSpecialties.PediatricImmunology,
                          new KeyValuePair<string, string>("418535003", "Pediatric immunology")
                          },
                      {C80ClinicalSpecialties.Cardiology, new KeyValuePair<string, string>("394579002", "Cardiology")},
                      {
                          C80ClinicalSpecialties.PediatricEndocrinology,
                          new KeyValuePair<string, string>("419610006", "Pediatric endocrinology")
                          },
                      {
                          C80ClinicalSpecialties.PediatricDentistry,
                          new KeyValuePair<string, string>("394607009", "Pediatric dentistry")
                          },
                      {
                          C80ClinicalSpecialties.Psychotherapy,
                          new KeyValuePair<string, string>("394913002", "Psychotherapy")
                          },
                      {
                          C80ClinicalSpecialties.PediatricHematology,
                          new KeyValuePair<string, string>("418652005", "Pediatric hematology")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryCardiothoracicTransplantation,
                          new KeyValuePair<string, string>("408464004", "Surgery-Cardiothoracic transplantation")
                          },
                      {
                          C80ClinicalSpecialties.GynecologicalOncology,
                          new KeyValuePair<string, string>("408446006", "Gynecological oncology")
                          },
                      {
                          C80ClinicalSpecialties.DiabeticMedicine,
                          new KeyValuePair<string, string>("408475000", "Diabetic medicine")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryCardiacSurgery,
                          new KeyValuePair<string, string>("408471009", "Surgery-Cardiac surgery")
                          },
                      {
                          C80ClinicalSpecialties.PublicHealthMedicine,
                          new KeyValuePair<string, string>("408440000", "Public health medicine")
                          },
                      {
                          C80ClinicalSpecialties.GenitoUrinaryMedicine,
                          new KeyValuePair<string, string>("394808002", "Genito-urinary medicine")
                          },
                      {
                          C80ClinicalSpecialties.SurgeryBreastSurgery,
                          new KeyValuePair<string, string>("408469009", "Surgery-Breast surgery")
                          },
                      {
                          C80ClinicalSpecialties.FamilyPractice,
                          new KeyValuePair<string, string>("419772000", "Family practice")
                          },
                      {
                          C80ClinicalSpecialties.GeneralPractice,
                          new KeyValuePair<string, string>("394814009", "General practice")
                          },
                      {
                          C80ClinicalSpecialties.GeneralMedicine,
                          new KeyValuePair<string, string>("394802001", "General medicine")
                          },
                      {C80ClinicalSpecialties.SurgeryEar, new KeyValuePair<string, string>("394605001", "Surgery-Ear")}
                  };

        /// <summary>
        /// Decodes a code enumeration to the associated code/label pairl
        /// </summary>
        public static KeyValuePair<string,string> Decode(C80ClinicalSpecialties code)
        {
            return m_C80ClinicalSpecialties[code];
        }
    }
}