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
    institutionName: string;
    institutionLogo: string;
};

export type AccountDto = {
    externalId: string;
    name: string;
    iban: string;
    currency: string;
    expirationDate: Date;
    institutionName: string;
    institutionLogo: string;
}