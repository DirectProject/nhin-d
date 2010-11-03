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
    /// This is the code representing the type of organizational setting where the clinical encounter, service,
    /// interaction, or treatment occurred
    /// </summary>
    public enum C80FacilityCodes
    {
        /// <summary>
        /// C80 code for Hospital ship (2081004)
        /// </summary>
        HospitalShip,
        /// <summary>
        /// C80 code for Walk-in clinic (81234003)
        /// </summary>
        WalkInClinic,
        /// <summary>
        /// C80 code for Free-standing mental health center (51563005)
        /// </summary>
        FreeStandingMentalHealthCenter,
        /// <summary>
        /// C80 code for Hospital-children's (82242000)
        /// </summary>
        HospitalChildrens,
        /// <summary>
        /// C80 code for Hospital outpatient orthopedics clinic (78001009)
        /// </summary>
        HospitalOutpatientOrthopedicsClinic,
        /// <summary>
        /// C80 code for Free-standing ambulatory surgery facility (10531005)
        /// </summary>
        FreeStandingAmbulatorySurgeryFacility,
        /// <summary>
        /// C80 code for Hospital outpatient endocrinology clinic (73644007)
        /// </summary>
        HospitalOutpatientEndocrinologyClinic,
        /// <summary>
        /// C80 code for Hospital outpatient general surgery clinic (90484001)
        /// </summary>
        HospitalOutpatientGeneralSurgeryClinic,
        /// <summary>
        /// C80 code for Hospital birthing center (52668009)
        /// </summary>
        HospitalBirthingCenter,
        /// <summary>
        /// C80 code for Other Hospital-based outpatient clinic or department (33022008)
        /// </summary>
        OtherHospitalBasedOutpatientClinicOrDepartment,
        /// <summary>
        /// C80 code for Hospital outpatient allergy clinic (360957003)
        /// </summary>
        HospitalOutpatientAllergyClinic,
        /// <summary>
        /// C80 code for Hospital-prison (224687002)
        /// </summary>
        HospitalPrison,
        /// <summary>
        /// C80 code for Hospital outpatient mental health center (14866005)
        /// </summary>
        HospitalOutpatientMentalHealthCenter,
        /// <summary>
        /// C80 code for Sexually transmitted disease health center (25681007)
        /// </summary>
        SexuallyTransmittedDiseaseHealthCenter,
        /// <summary>
        /// C80 code for Private residential home (310205006)
        /// </summary>
        PrivateResidentialHome,
        /// <summary>
        /// C80 code for Hospital outpatient hematology clinic (56293002)
        /// </summary>
        HospitalOutpatientHematologyClinic,
        /// <summary>
        /// C80 code for Residential school infirmary (39913001)
        /// </summary>
        ResidentialSchoolInfirmary,
        /// <summary>
        /// C80 code for Rural health center (77931003)
        /// </summary>
        RuralHealthCenter,
        /// <summary>
        /// C80 code for Local community health center (6827000)
        /// </summary>
        LocalCommunityHealthCenter,
        /// <summary>
        /// C80 code for Other Independent ambulatory care provider site (394759007)
        /// </summary>
        OtherIndependentAmbulatoryCareProviderSite,
        /// <summary>
        /// C80 code for Hospital-government (79993009)
        /// </summary>
        HospitalGovernment,
        /// <summary>
        /// C80 code for Vaccination clinic (46224007)
        /// </summary>
        VaccinationClinic,
        /// <summary>
        /// C80 code for Skilled nursing facility (45618002)
        /// </summary>
        SkilledNursingFacility,
        /// <summary>
        /// C80 code for Hospital outpatient immunology clinic (360966004)
        /// </summary>
        HospitalOutpatientImmunologyClinic,
        /// <summary>
        /// C80 code for Residential institution (419955002)
        /// </summary>
        ResidentialInstitution,
        /// <summary>
        /// C80 code for Child day care center (413817003)
        /// </summary>
        ChildDayCareCenter,
        /// <summary>
        /// C80 code for Hospital outpatient infectious disease clinic (2849009)
        /// </summary>
        HospitalOutpatientInfectiousDiseaseClinic,
        /// <summary>
        /// C80 code for Hospital outpatient neurology clinic (38238005)
        /// </summary>
        HospitalOutpatientNeurologyClinic,
        /// <summary>
        /// C80 code for Ambulatory surgery center (405607001)
        /// </summary>
        AmbulatorySurgeryCenter,
        /// <summary>
        /// C80 code for Hospital outpatient family medicine clinic (31628002)
        /// </summary>
        HospitalOutpatientFamilyMedicineClinic,
        /// <summary>
        /// C80 code for Other Ambulatory care site (35971002)
        /// </summary>
        OtherAmbulatoryCareSite,
        /// <summary>
        /// C80 code for Hospice facility (284546000)
        /// </summary>
        HospiceFacility,
        /// <summary>
        /// C80 code for Hospital outpatient pain clinic (36293008)
        /// </summary>
        HospitalOutpatientPainClinic,
        /// <summary>
        /// C80 code for Hospital outpatient rehabilitation clinic (37546005)
        /// </summary>
        HospitalOutpatientRehabilitationClinic,
        /// <summary>
        /// C80 code for Emergency department--hospital (73770003)
        /// </summary>
        EmergencyDepartment_hospital,
        /// <summary>
        /// C80 code for Hospital outpatient ophthalmology clinic (78088001)
        /// </summary>
        HospitalOutpatientOphthalmologyClinic,
        /// <summary>
        /// C80 code for Hospital outpatient gastroenterology clinic (58482006)
        /// </summary>
        HospitalOutpatientGastroenterologyClinic,
        /// <summary>
        /// C80 code for Free-standing birthing center (91154008)
        /// </summary>
        FreeStandingBirthingCenter,
        /// <summary>
        /// C80 code for Contained casualty setting (409519008)
        /// </summary>
        ContainedCasualtySetting,
        /// <summary>
        /// C80 code for Dialysis unit--hospital (418518002)
        /// </summary>
        DialysisUnit_hospital,
        /// <summary>
        /// C80 code for Hospital outpatient dental clinic (10206005)
        /// </summary>
        HospitalOutpatientDentalClinic,
        /// <summary>
        /// C80 code for Free-standing geriatric health center (41844007)
        /// </summary>
        FreeStandingGeriatricHealthCenter,
        /// <summary>
        /// C80 code for Hospital outpatient peripheral vascular clinic (5584006)
        /// </summary>
        HospitalOutpatientPeripheralVascularClinic,
        /// <summary>
        /// C80 code for Hospital-community (225732001)
        /// </summary>
        HospitalCommunity,
        /// <summary>
        /// C80 code for Hospital outpatient urology clinic (50569004)
        /// </summary>
        HospitalOutpatientUrologyClinic,
        /// <summary>
        /// C80 code for Hospital outpatient respiratory disease clinic (57159002)
        /// </summary>
        HospitalOutpatientRespiratoryDiseaseClinic,
        /// <summary>
        /// C80 code for Free-standing radiology facility (1773006)
        /// </summary>
        FreeStandingRadiologyFacility,
        /// <summary>
        /// C80 code for Hospital outpatient obstetrical clinic (56189001)
        /// </summary>
        HospitalOutpatientObstetricalClinic,
        /// <summary>
        /// C80 code for Care of the elderly day hospital (309900005)
        /// </summary>
        CareOfTheElderlyDayHospital,
        /// <summary>
        /// C80 code for Hospital outpatient gynecology clinic (22549003)
        /// </summary>
        HospitalOutpatientGynecologyClinic,
        /// <summary>
        /// C80 code for Health maintenance organization (72311000)
        /// </summary>
        HealthMaintenanceOrganization,
        /// <summary>
        /// C80 code for Substance abuse treatment center (20078004)
        /// </summary>
        SubstanceAbuseTreatmentCenter,
        /// <summary>
        /// C80 code for Hospital-Veterans' Administration (48311003)
        /// </summary>
        HospitalVeteransAdministration,
        /// <summary>
        /// C80 code for Hospital-long term care (32074000)
        /// </summary>
        HospitalLongTermCare,
        /// <summary>
        /// C80 code for Hospital-trauma center (36125001)
        /// </summary>
        HospitalTraumaCenter,
        /// <summary>
        /// C80 code for Solo practice private office (83891005)
        /// </summary>
        SoloPracticePrivateOffice,
        /// <summary>
        /// C80 code for Helicopter-based care (901005)
        /// </summary>
        HelicopterBasedCare,
        /// <summary>
        /// C80 code for Hospital-military field (4322002)
        /// </summary>
        HospitalMilitaryField,
        /// <summary>
        /// C80 code for Hospital outpatient geriatric health center (1814000)
        /// </summary>
        HospitalOutpatientGeriatricHealthCenter,
        /// <summary>
        /// C80 code for Hospital outpatient pediatric clinic (3729002)
        /// </summary>
        HospitalOutpatientPediatricClinic,
        /// <summary>
        /// C80 code for Private physicians' group office (39350007)
        /// </summary>
        PrivatePhysiciansGroupOffice,
        /// <summary>
        /// C80 code for Sports facility (272501009)
        /// </summary>
        SportsFacility,
        /// <summary>
        /// C80 code for Adult day care center (413456002)
        /// </summary>
        AdultDayCareCenter,
        /// <summary>
        /// C80 code for Traveler's aid clinic (59374000)
        /// </summary>
        TravelersAidClinic,
        /// <summary>
        /// C80 code for Fee-for-service private physicians' group office (19602009)
        /// </summary>
        FeeForServicePrivatePhysiciansGroupOffice,
        /// <summary>
        /// C80 code for Hospital outpatient dermatology clinic (37550003)
        /// </summary>
        HospitalOutpatientDermatologyClinic,
        /// <summary>
        /// C80 code for Elderly assessment clinic (275576008)
        /// </summary>
        ElderlyAssessmentClinic,
        /// <summary>
        /// C80 code for Hospital outpatient rheumatology clinic (331006)
        /// </summary>
        HospitalOutpatientRheumatologyClinic,
        /// <summary>
        /// C80 code for Hospital radiology facility (79491001)
        /// </summary>
        HospitalRadiologyFacility,
        /// <summary>
        /// C80 code for Free-standing laboratory facility (45899008)
        /// </summary>
        FreeStandingLaboratoryFacility,
        /// <summary>
        /// C80 code for Other Health encounter site (394777002)
        /// </summary>
        OtherHealthEncounterSite,
        /// <summary>
        /// C80 code for Ambulance-based care (11424001)
        /// </summary>
        AmbulanceBasedCare,
        /// <summary>
        /// C80 code for Hospital outpatient oncology clinic (89972002)
        /// </summary>
        HospitalOutpatientOncologyClinic,
        /// <summary>
        /// C80 code for Hospital-psychiatric (62480006)
        /// </summary>
        HospitalPsychiatric,
        /// <summary>
        /// C80 code for Hospital ambulatory surgery facility (69362002)
        /// </summary>
        HospitalAmbulatorySurgeryFacility,
        /// <summary>
        /// C80 code for Hospital-rehabilitation (80522000)
        /// </summary>
        HospitalRehabilitation,
        /// <summary>
        /// C80 code for Hospital outpatient otorhinolaryngology clinic (23392004)
        /// </summary>
        HospitalOutpatientOtorhinolaryngologyClinic,
        /// <summary>
        /// C80 code for Psychogeriatric day hospital (309898008)
        /// </summary>
        PsychogeriatricDayHospital,
        /// <summary>
        /// C80 code for Nursing home (42665001)
        /// </summary>
        NursingHome,
    }

    /// <summary>
    /// Represents a facility code
    /// </summary>
    public static class C80FacilityCodeUtils
    {
        /// <summary>
        /// Returns a <see cref="CodedValue"/> for the code
        /// </summary>
        public static CodedValue ToCodedValue(this C80FacilityCodes code)
        {
            KeyValuePair<string, string> pair = Decode(code);
            return new CodedValue(pair.Key, pair.Value, "2.16.840.1.113883.3.88.12.80.67");
        }


        private static Dictionary<C80FacilityCodes, KeyValuePair<string, string>> m_C80FacilityCodes
            = new Dictionary<C80FacilityCodes, KeyValuePair<string, string>>()
                  {
                      {C80FacilityCodes.HospitalShip, new KeyValuePair<string, string>("2081004", "Hospital ship")},
                      {C80FacilityCodes.WalkInClinic, new KeyValuePair<string, string>("81234003", "Walk-in clinic")},
                      {
                          C80FacilityCodes.FreeStandingMentalHealthCenter,
                          new KeyValuePair<string, string>("51563005", "Free-standing mental health center")
                          },
                      {
                          C80FacilityCodes.HospitalChildrens,
                          new KeyValuePair<string, string>("82242000", "Hospital-children's")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientOrthopedicsClinic,
                          new KeyValuePair<string, string>("78001009", "Hospital outpatient orthopedics clinic")
                          },
                      {
                          C80FacilityCodes.FreeStandingAmbulatorySurgeryFacility,
                          new KeyValuePair<string, string>("10531005", "Free-standing ambulatory surgery facility")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientEndocrinologyClinic,
                          new KeyValuePair<string, string>("73644007", "Hospital outpatient endocrinology clinic")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientGeneralSurgeryClinic,
                          new KeyValuePair<string, string>("90484001", "Hospital outpatient general surgery clinic")
                          },
                      {
                          C80FacilityCodes.HospitalBirthingCenter,
                          new KeyValuePair<string, string>("52668009", "Hospital birthing center")
                          },
                      {
                          C80FacilityCodes.OtherHospitalBasedOutpatientClinicOrDepartment,
                          new KeyValuePair<string, string>("33022008",
                                                           "Other Hospital-based outpatient clinic or department")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientAllergyClinic,
                          new KeyValuePair<string, string>("360957003", "Hospital outpatient allergy clinic")
                          },
                      {
                          C80FacilityCodes.HospitalPrison, new KeyValuePair<string, string>("224687002", "Hospital-prison")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientMentalHealthCenter,
                          new KeyValuePair<string, string>("14866005", "Hospital outpatient mental health center")
                          },
                      {
                          C80FacilityCodes.SexuallyTransmittedDiseaseHealthCenter,
                          new KeyValuePair<string, string>("25681007", "Sexually transmitted disease health center")
                          },
                      {
                          C80FacilityCodes.PrivateResidentialHome,
                          new KeyValuePair<string, string>("310205006", "Private residential home")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientHematologyClinic,
                          new KeyValuePair<string, string>("56293002", "Hospital outpatient hematology clinic")
                          },
                      {
                          C80FacilityCodes.ResidentialSchoolInfirmary,
                          new KeyValuePair<string, string>("39913001", "Residential school infirmary")
                          },
                      {
                          C80FacilityCodes.RuralHealthCenter,
                          new KeyValuePair<string, string>("77931003", "Rural health center")
                          },
                      {
                          C80FacilityCodes.LocalCommunityHealthCenter,
                          new KeyValuePair<string, string>("6827000", "Local community health center")
                          },
                      {
                          C80FacilityCodes.OtherIndependentAmbulatoryCareProviderSite,
                          new KeyValuePair<string, string>("394759007",
                                                           "Other Independent ambulatory care provider site")
                          },
                      {
                          C80FacilityCodes.HospitalGovernment,
                          new KeyValuePair<string, string>("79993009", "Hospital-government")
                          },
                      {
                          C80FacilityCodes.VaccinationClinic,
                          new KeyValuePair<string, string>("46224007", "Vaccination clinic")
                          },
                      {
                          C80FacilityCodes.SkilledNursingFacility,
                          new KeyValuePair<string, string>("45618002", "Skilled nursing facility")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientImmunologyClinic,
                          new KeyValuePair<string, string>("360966004", "Hospital outpatient immunology clinic")
                          },
                      {
                          C80FacilityCodes.ResidentialInstitution,
                          new KeyValuePair<string, string>("419955002", "Residential institution")
                          },
                      {
                          C80FacilityCodes.ChildDayCareCenter,
                          new KeyValuePair<string, string>("413817003", "Child day care center")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientInfectiousDiseaseClinic,
                          new KeyValuePair<string, string>("2849009", "Hospital outpatient infectious disease clinic")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientNeurologyClinic,
                          new KeyValuePair<string, string>("38238005", "Hospital outpatient neurology clinic")
                          },
                      {
                          C80FacilityCodes.AmbulatorySurgeryCenter,
                          new KeyValuePair<string, string>("405607001", "Ambulatory surgery center")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientFamilyMedicineClinic,
                          new KeyValuePair<string, string>("31628002", "Hospital outpatient family medicine clinic")
                          },
                      {
                          C80FacilityCodes.OtherAmbulatoryCareSite,
                          new KeyValuePair<string, string>("35971002", "Other Ambulatory care site")
                          },
                      {
                          C80FacilityCodes.HospiceFacility,
                          new KeyValuePair<string, string>("284546000", "Hospice facility")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientPainClinic,
                          new KeyValuePair<string, string>("36293008", "Hospital outpatient pain clinic")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientRehabilitationClinic,
                          new KeyValuePair<string, string>("37546005", "Hospital outpatient rehabilitation clinic")
                          },
                      {
                          C80FacilityCodes.EmergencyDepartment_hospital,
                          new KeyValuePair<string, string>("73770003", "Emergency department--hospital")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientOphthalmologyClinic,
                          new KeyValuePair<string, string>("78088001", "Hospital outpatient ophthalmology clinic")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientGastroenterologyClinic,
                          new KeyValuePair<string, string>("58482006", "Hospital outpatient gastroenterology clinic")
                          },
                      {
                          C80FacilityCodes.FreeStandingBirthingCenter,
                          new KeyValuePair<string, string>("91154008", "Free-standing birthing center")
                          },
                      {
                          C80FacilityCodes.ContainedCasualtySetting,
                          new KeyValuePair<string, string>("409519008", "Contained casualty setting")
                          },
                      {
                          C80FacilityCodes.DialysisUnit_hospital,
                          new KeyValuePair<string, string>("418518002", "Dialysis unit--hospital")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientDentalClinic,
                          new KeyValuePair<string, string>("10206005", "Hospital outpatient dental clinic")
                          },
                      {
                          C80FacilityCodes.FreeStandingGeriatricHealthCenter,
                          new KeyValuePair<string, string>("41844007", "Free-standing geriatric health center")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientPeripheralVascularClinic,
                          new KeyValuePair<string, string>("5584006", "Hospital outpatient peripheral vascular clinic")
                          },
                      {
                          C80FacilityCodes.HospitalCommunity,
                          new KeyValuePair<string, string>("225732001", "Hospital-community")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientUrologyClinic,
                          new KeyValuePair<string, string>("50569004", "Hospital outpatient urology clinic")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientRespiratoryDiseaseClinic,
                          new KeyValuePair<string, string>("57159002", "Hospital outpatient respiratory disease clinic")
                          },
                      {
                          C80FacilityCodes.FreeStandingRadiologyFacility,
                          new KeyValuePair<string, string>("1773006", "Free-standing radiology facility")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientObstetricalClinic,
                          new KeyValuePair<string, string>("56189001", "Hospital outpatient obstetrical clinic")
                          },
                      {
                          C80FacilityCodes.CareOfTheElderlyDayHospital,
                          new KeyValuePair<string, string>("309900005", "Care of the elderly day hospital")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientGynecologyClinic,
                          new KeyValuePair<string, string>("22549003", "Hospital outpatient gynecology clinic")
                          },
                      {
                          C80FacilityCodes.HealthMaintenanceOrganization,
                          new KeyValuePair<string, string>("72311000", "Health maintenance organization")
                          },
                      {
                          C80FacilityCodes.SubstanceAbuseTreatmentCenter,
                          new KeyValuePair<string, string>("20078004", "Substance abuse treatment center")
                          },
                      {
                          C80FacilityCodes.HospitalVeteransAdministration,
                          new KeyValuePair<string, string>("48311003", "Hospital-Veterans' Administration")
                          },
                      {
                          C80FacilityCodes.HospitalLongTermCare,
                          new KeyValuePair<string, string>("32074000", "Hospital-long term care")
                          },
                      {
                          C80FacilityCodes.HospitalTraumaCenter,
                          new KeyValuePair<string, string>("36125001", "Hospital-trauma center")
                          },
                      {
                          C80FacilityCodes.SoloPracticePrivateOffice,
                          new KeyValuePair<string, string>("83891005", "Solo practice private office")
                          },
                      {
                          C80FacilityCodes.HelicopterBasedCare,
                          new KeyValuePair<string, string>("901005", "Helicopter-based care")
                          },
                      {
                          C80FacilityCodes.HospitalMilitaryField,
                          new KeyValuePair<string, string>("4322002", "Hospital-military field")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientGeriatricHealthCenter,
                          new KeyValuePair<string, string>("1814000", "Hospital outpatient geriatric health center")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientPediatricClinic,
                          new KeyValuePair<string, string>("3729002", "Hospital outpatient pediatric clinic")
                          },
                      {
                          C80FacilityCodes.PrivatePhysiciansGroupOffice,
                          new KeyValuePair<string, string>("39350007", "Private physicians' group office")
                          },
                      {
                          C80FacilityCodes.SportsFacility, new KeyValuePair<string, string>("272501009", "Sports facility")
                          },
                      {
                          C80FacilityCodes.AdultDayCareCenter,
                          new KeyValuePair<string, string>("413456002", "Adult day care center")
                          },
                      {
                          C80FacilityCodes.TravelersAidClinic,
                          new KeyValuePair<string, string>("59374000", "Traveler's aid clinic")
                          },
                      {
                          C80FacilityCodes.FeeForServicePrivatePhysiciansGroupOffice,
                          new KeyValuePair<string, string>("19602009",
                                                           "Fee-for-service private physicians' group office")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientDermatologyClinic,
                          new KeyValuePair<string, string>("37550003", "Hospital outpatient dermatology clinic")
                          },
                      {
                          C80FacilityCodes.ElderlyAssessmentClinic,
                          new KeyValuePair<string, string>("275576008", "Elderly assessment clinic")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientRheumatologyClinic,
                          new KeyValuePair<string, string>("331006", "Hospital outpatient rheumatology clinic")
                          },
                      {
                          C80FacilityCodes.HospitalRadiologyFacility,
                          new KeyValuePair<string, string>("79491001", "Hospital radiology facility")
                          },
                      {
                          C80FacilityCodes.FreeStandingLaboratoryFacility,
                          new KeyValuePair<string, string>("45899008", "Free-standing laboratory facility")
                          },
                      {
                          C80FacilityCodes.OtherHealthEncounterSite,
                          new KeyValuePair<string, string>("394777002", "Other Health encounter site")
                          },
                      {
                          C80FacilityCodes.AmbulanceBasedCare,
                          new KeyValuePair<string, string>("11424001", "Ambulance-based care")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientOncologyClinic,
                          new KeyValuePair<string, string>("89972002", "Hospital outpatient oncology clinic")
                          },
                      {
                          C80FacilityCodes.HospitalPsychiatric,
                          new KeyValuePair<string, string>("62480006", "Hospital-psychiatric")
                          },
                      {
                          C80FacilityCodes.HospitalAmbulatorySurgeryFacility,
                          new KeyValuePair<string, string>("69362002", "Hospital ambulatory surgery facility")
                          },
                      {
                          C80FacilityCodes.HospitalRehabilitation,
                          new KeyValuePair<string, string>("80522000", "Hospital-rehabilitation")
                          },
                      {
                          C80FacilityCodes.HospitalOutpatientOtorhinolaryngologyClinic,
                          new KeyValuePair<string, string>("23392004", "Hospital outpatient otorhinolaryngology clinic")
                          },
                      {
                          C80FacilityCodes.PsychogeriatricDayHospital,
                          new KeyValuePair<string, string>("309898008", "Psychogeriatric day hospital")
                          },
                      {C80FacilityCodes.NursingHome, new KeyValuePair<string, string>("42665001", "Nursing home")}
                  };

        /// <summary>
        /// Returns the code/label pair for the provided enumeration code
        /// </summary>
        public static KeyValuePair<string, string> Decode (C80FacilityCodes code)
        {
            return m_C80FacilityCodes[code];
        }

    }
}