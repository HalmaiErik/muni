export type InstitutionDto = {
    id: string;
    name: string;
    logo: string;
};

export type CreateRequisitionRequest = {
    institutionId: string;
    redirectUrl: string;
};

export type RequisitionDto = {
    id: string;
    link: string;
};

export type CreateCustomerRequest = {
    email: string;
    requisitionId: string;
};

export type AccountDetailsDto = {
    resourceId: string;
    iban: string;
    currency: string;
    name: string;
}