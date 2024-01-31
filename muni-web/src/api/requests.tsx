export type CreateRequisitionRequest = {
    institutionId: string;
    redirectUrl: string;
};

export type CreateCustomerRequest = {
    email: string;
    requisitionId: string;
    institutionName: string;
    institutionLogo: string;
};

export type AccountFullInfoRequest = {
    email: string;
    accountExternalId: string;
    refresh: boolean;
}