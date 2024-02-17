export type CreateRequisitionRequest = {
    institutionId: string;
    redirectUrl: string;
};

export type CreateCustomerRequest = {
    requisitionId: string;
    institutionName: string;
    institutionLogo: string;
};

export type EditTransactionCategories = {
    transactionExternalId: string;
    categoryIds: number[];
};

export type CategorizeAccountTransactionsRequest = {
    accountExternalId: string;
    categoryId: number;
};