
export interface Construction {
    id: string;
    name: string;
}
export interface CurrentConstructionRequest {
  id: number;
}

export interface CalcResult {
    year: number,
    interest: number,
    valueEndOfYear: number,
    annualInvestment: number,
    totalInterest: number,
    totalAmountInvested: number
}
