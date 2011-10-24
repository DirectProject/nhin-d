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
    /// HITSP C80 class codes (table 2-144)
    /// </summary>
    public enum C80ClassCode
    {
        /// <summary>
        /// C80 code for Privacy Policy (57017-6)
        /// </summary>
        PrivacyPolicy,
        /// <summary>
        /// C80 code for Ophthalmology Studies (28619-5)
        /// </summary>
        OphthalmologyStudies,
        /// <summary>
        /// C80 code for Nursery Records (11543-6)
        /// </summary>
        NurseryRecords,
        /// <summary>
        /// C80 code for Anesthesia Records (11485-0)
        /// </summary>
        AnesthesiaRecords,
        /// <summary>
        /// C80 code for History of Immunization (11369-6)
        /// </summary>
        HistoryOfImmunization,
        /// <summary>
        /// C80 code for Personal health monitoring report (53576-5)
        /// </summary>
        PersonalHealthMonitoringReport,
        /// <summary>
        /// C80 code for Privacy Policy Acknowledgment (57016-8)
        /// </summary>
        PrivacyPolicyAcknowledgment,
        /// <summary>
        /// C80 code for Counseling note (47042-7)
        /// </summary>
        CounselingNote,
        /// <summary>
        /// C80 code for Pre-operative evaluation and management note (34775-7)
        /// </summary>
        PreOperativeEvaluationAndManagementNote,
        /// <summary>
        /// C80 code for Pathology procedure note (34122-2)
        /// </summary>
        PathologyProcedureNote,
        /// <summary>
        /// C80 code for Pulmonary Studies (27896-0)
        /// </summary>
        PulmonaryStudies,
        /// <summary>
        /// C80 code for Transfer summarization note (18761-7)
        /// </summary>
        TransferSummarizationNote,
        /// <summary>
        /// C80 code for Subsequent evaluation note (11506-3)
        /// </summary>
        SubsequentEvaluationNote,
        /// <summary>
        /// C80 code for Interventional procedure note (34121-4)
        /// </summary>
        InterventionalProcedureNote,
        /// <summary>
        /// C80 code for Gastroenterology Endoscopy Studies (27895-2)
        /// </summary>
        GastroenterologyEndoscopyStudies,
        /// <summary>
        /// C80 code for Medication Summary (56445-0)
        /// </summary>
        MedicationSummary,
        /// <summary>
        /// C80 code for Non-patient Communication (47049-2)
        /// </summary>
        NonPatientCommunication,
        /// <summary>
        /// C80 code for Telephone encounter note (34748-4)
        /// </summary>
        TelephoneEncounterNote,
        /// <summary>
        /// C80 code for Obstetrical Studies (26442-4)
        /// </summary>
        ObstetricalStudies,
        /// <summary>
        /// C80 code for Discharge summarization note (18842-5)
        /// </summary>
        DischargeSummarizationNote,
        /// <summary>
        /// C80 code for Radiology Studies (18726-0)
        /// </summary>
        RadiologyStudies,
        /// <summary>
        /// C80 code for Labor And Delivery Records (15508-5)
        /// </summary>
        LaborAndDeliveryRecords,
        /// <summary>
        /// C80 code for Miscellaneous Studies (28634-4)
        /// </summary>
        MiscellaneousStudies,
        /// <summary>
        /// C80 code for Procedure note (28570-0)
        /// </summary>
        ProcedureNote,
        /// <summary>
        /// C80 code for Cardiology Studies (26441-6)
        /// </summary>
        CardiologyStudies,
        /// <summary>
        /// C80 code for Admission history and physical note (47039-3)
        /// </summary>
        AdmissionHistoryAndPhysicalNote,
        /// <summary>
        /// C80 code for History and physical note (34117-2)
        /// </summary>
        HistoryAndPhysicalNote,
        /// <summary>
        /// C80 code for Evaluation and management note (34109-9)
        /// </summary>
        EvaluationAndManagementNote,
        /// <summary>
        /// C80 code for Dialysis Records (29749-9)
        /// </summary>
        DialysisRecords,
        /// <summary>
        /// C80 code for Summary of death (47046-8)
        /// </summary>
        SummaryOfDeath,
        /// <summary>
        /// C80 code for Consultation note (11488-4)
        /// </summary>
        ConsultationNote,
        /// <summary>
        /// C80 code for Study report (47045-0)
        /// </summary>
        StudyReport,
        /// <summary>
        /// C80 code for Summarization of episode note (34133-9)
        /// </summary>
        SummarizationOfEpisodeNote,
        /// <summary>
        /// C80 code for Perioperative Records (29752-3)
        /// </summary>
        PerioperativeRecords,
        /// <summary>
        /// C80 code for Laboratory Studies (26436-6)
        /// </summary>
        LaboratoryStudies,
        /// <summary>
        /// C80 code for Critical Care Records (29751-5)
        /// </summary>
        CriticalCareRecords,
        /// <summary>
        /// C80 code for Pathology Studies (27898-6)
        /// </summary>
        PathologyStudies,
        /// <summary>
        /// C80 code for Chemotherapy Records (11486-8)
        /// </summary>
        ChemotherapyRecords,
        /// <summary>
        /// C80 code for Transfer of care referral note (34140-4)
        /// </summary>
        TransferOfCareReferralNote,
        /// <summary>
        /// C80 code for Neonatal Intensive Care Records (29750-7)
        /// </summary>
        NeonatalIntensiveCareRecords,
        /// <summary>
        /// C80 code for Neuromuscular Electrophysiology Studies (27897-8)
        /// </summary>
        NeuromuscularElectrophysiologyStudies
    }

    /// <summary>
    /// Represents the class code of a document.
    /// </summary>
    public static class C80ClassCodeUtils
    {


        /// <summary>
        /// Returns a <see cref="CodedValue"/> for the code
        /// </summary>
        public static CodedValue ToCodedValue(this C80ClassCode code)
        {
            KeyValuePair<string, string> pair = Decode(code);
            return new CodedValue(pair.Key, pair.Value, "2.16.840.1.113883.3.88.12.80.46");
        }

        private static Dictionary<C80ClassCode, KeyValuePair<string, string>> m_C80DocumentClass_mappings
            = new Dictionary<C80ClassCode, KeyValuePair<string, string>>()
                  {
                      {C80ClassCode.PrivacyPolicy, new KeyValuePair<string, string>("57017-6", "Privacy Policy")},
                      {
                          C80ClassCode.OphthalmologyStudies,
                          new KeyValuePair<string, string>("28619-5", "Ophthalmology Studies")
                          },
                      {C80ClassCode.NurseryRecords, new KeyValuePair<string, string>("11543-6", "Nursery Records")},
                      {
                          C80ClassCode.AnesthesiaRecords, new KeyValuePair<string, string>("11485-0", "Anesthesia Records")
                          },
                      {
                          C80ClassCode.HistoryOfImmunization,
                          new KeyValuePair<string, string>("11369-6", "History of Immunization")
                          },
                      {
                          C80ClassCode.PersonalHealthMonitoringReport,
                          new KeyValuePair<string, string>("53576-5", "Personal health monitoring report")
                          },
                      {
                          C80ClassCode.PrivacyPolicyAcknowledgment,
                          new KeyValuePair<string, string>("57016-8", "Privacy Policy Acknowledgment")
                          },
                      {C80ClassCode.CounselingNote, new KeyValuePair<string, string>("47042-7", "Counseling note")},
                      {
                          C80ClassCode.PreOperativeEvaluationAndManagementNote,
                          new KeyValuePair<string, string>("34775-7", "Pre-operative evaluation and management note")
                          },
                      {
                          C80ClassCode.PathologyProcedureNote,
                          new KeyValuePair<string, string>("34122-2", "Pathology procedure note")
                          },
                      {C80ClassCode.PulmonaryStudies, new KeyValuePair<string, string>("27896-0", "Pulmonary Studies")},
                      {
                          C80ClassCode.TransferSummarizationNote,
                          new KeyValuePair<string, string>("18761-7", "Transfer summarization note")
                          },
                      {
                          C80ClassCode.SubsequentEvaluationNote,
                          new KeyValuePair<string, string>("11506-3", "Subsequent evaluation note")
                          },
                      {
                          C80ClassCode.InterventionalProcedureNote,
                          new KeyValuePair<string, string>("34121-4", "Interventional procedure note")
                          },
                      {
                          C80ClassCode.GastroenterologyEndoscopyStudies,
                          new KeyValuePair<string, string>("27895-2", "Gastroenterology Endoscopy Studies")
                          },
                      {
                          C80ClassCode.MedicationSummary, new KeyValuePair<string, string>("56445-0", "Medication Summary")
                          },
                      {
                          C80ClassCode.NonPatientCommunication,
                          new KeyValuePair<string, string>("47049-2", "Non-patient Communication")
                          },
                      {
                          C80ClassCode.TelephoneEncounterNote,
                          new KeyValuePair<string, string>("34748-4", "Telephone encounter note")
                          },
                      {
                          C80ClassCode.ObstetricalStudies,
                          new KeyValuePair<string, string>("26442-4", "Obstetrical Studies")
                          },
                      {
                          C80ClassCode.DischargeSummarizationNote,
                          new KeyValuePair<string, string>("18842-5", "Discharge summarization note")
                          },
                      {C80ClassCode.RadiologyStudies, new KeyValuePair<string, string>("18726-0", "Radiology Studies")},
                      {
                          C80ClassCode.LaborAndDeliveryRecords,
                          new KeyValuePair<string, string>("15508-5", "Labor And Delivery Records")
                          },
                      {
                          C80ClassCode.MiscellaneousStudies,
                          new KeyValuePair<string, string>("28634-4", "Miscellaneous Studies")
                          },
                      {C80ClassCode.ProcedureNote, new KeyValuePair<string, string>("28570-0", "Procedure note")},
                      {
                          C80ClassCode.CardiologyStudies, new KeyValuePair<string, string>("26441-6", "Cardiology Studies")
                          },
                      {
                          C80ClassCode.AdmissionHistoryAndPhysicalNote,
                          new KeyValuePair<string, string>("47039-3", "Admission history and physical note")
                          },
                      {
                          C80ClassCode.HistoryAndPhysicalNote,
                          new KeyValuePair<string, string>("34117-2", "History and physical note")
                          },
                      {
                          C80ClassCode.EvaluationAndManagementNote,
                          new KeyValuePair<string, string>("34109-9", "Evaluation and management note")
                          },
                      {C80ClassCode.DialysisRecords, new KeyValuePair<string, string>("29749-9", "Dialysis Records")},
                      {C80ClassCode.SummaryOfDeath, new KeyValuePair<string, string>("47046-8", "Summary of death")},
                      {C80ClassCode.ConsultationNote, new KeyValuePair<string, string>("11488-4", "Consultation note")},
                      {C80ClassCode.StudyReport, new KeyValuePair<string, string>("47045-0", "Study report")},
                      {
                          C80ClassCode.SummarizationOfEpisodeNote,
                          new KeyValuePair<string, string>("34133-9", "Summarization of episode note")
                          },
                      {
                          C80ClassCode.PerioperativeRecords,
                          new KeyValuePair<string, string>("29752-3", "Perioperative Records")
                          },
                      {
                          C80ClassCode.LaboratoryStudies, new KeyValuePair<string, string>("26436-6", "Laboratory Studies")
                          },
                      {
                          C80ClassCode.CriticalCareRecords,
                          new KeyValuePair<string, string>("29751-5", "Critical Care Records")
                          },
                      {C80ClassCode.PathologyStudies, new KeyValuePair<string, string>("27898-6", "Pathology Studies")},
                      {
                          C80ClassCode.ChemotherapyRecords,
                          new KeyValuePair<string, string>("11486-8", "Chemotherapy Records")
                          },
                      {
                          C80ClassCode.TransferOfCareReferralNote,
                          new KeyValuePair<string, string>("34140-4", "Transfer of care referral note")
                          },
                      {
                          C80ClassCode.NeonatalIntensiveCareRecords,
                          new KeyValuePair<string, string>("29750-7", "Neonatal Intensive Care Records")
                          },
                      {
                          C80ClassCode.NeuromuscularElectrophysiologyStudies,
                          new KeyValuePair<string, string>("27897-8", "Neuromuscular Electrophysiology Studies")
                          },
                  };

        /// <summary>
        /// Returns the code/label pair for the provided enumeration code
        /// </summary>
        public static KeyValuePair<string, string> Decode(C80ClassCode code)
        {
            return CodeDictionary[code];
        }

        /// <summary>
        /// A dictionary that maps from the enumeration to the associated code/value pair
        /// </summary>
        public static Dictionary<C80ClassCode, KeyValuePair<string, string>> CodeDictionary
        {
            get
            {
                return m_C80DocumentClass_mappings;
            }
        }
    }
}