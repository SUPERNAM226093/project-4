export interface PatientInfo {
  name?: string;
  patientId?: string;
  age?: string;
  gender?: string;
  dob?: string;
  accession?: string;
  location?: string;
}

export interface SampleInfo {
  collectedAt?: string;
  receivedAt?: string;
  reportedAt?: string;
  visitType?: string;
}

export interface ExamInfo {
  examType?: string;
  bodyPart?: string;
  view?: string;
  examDate?: string;
  facility?: string;
}

export interface TestResult {
  testName?: string;
  value?: string;
  unit?: string;
  referenceRange?: string;
  status?: string;
}

export interface AbnormalResult {
  testName?: string;
  value?: string;
  unit?: string;
  referenceRange?: string;
  status?: string;
}

export interface MedicalOcrResponse {
  documentType?: string;
  patientInfo?: PatientInfo;
  sampleInfo?: SampleInfo;
  examInfo?: ExamInfo;
  department?: string;
  tests?: TestResult[];
  abnormalResults?: AbnormalResult[];
  rawText?: string;
  uncertainFields?: string[];
  warning?: string;
}
