import { User } from "firebase/auth";
import { useMutation, useQuery, useQueryClient } from "react-query";
import { LOCAL_STORAGE_TOKEN_KEY } from "../utils/constants";
import { AccountDto, AccountFullInfoDto, CategoryDto, InstitutionDto, RequisitionDto, StatsDto } from "./dtos";
import { CategorizeAccountTransactionsRequest, CreateCustomerRequest, CreateRequisitionRequest, EditTransactionCategories, GetStatsRequest } from "./requests";

const baseUrl = 'http://localhost:8080/api/v1/bankaccount';

const useCountryInstitutions = (countryCode: string) => {
    return useQuery(
        'countryInstitutions',
        async () => {
            const response = await fetch(baseUrl + '/institutions/' + countryCode, {
                method: 'get',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`
                }),
            });
            const data: InstitutionDto[] = await response.json();
            return data;
        },
        { refetchOnWindowFocus: false }
    );
};

const useCreateRequisition = () => {
    return useMutation({
        mutationFn: async (request: CreateRequisitionRequest) => {
            const response = await fetch(baseUrl + '/requisitions', {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify(request)
            });
            const data: RequisitionDto = await response.json();
            return data;
        },
        onSuccess: (response) => {
            window.location.href = response.link;
        }
    });
};

const useCreateCustomer = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (request: CreateCustomerRequest) => {
            await fetch(baseUrl + '/customer/create', {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify(request)
            });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['customerAccounts'] })
        }
    });
};

const useCustomerAccounts = (currentUser: User | null) => {
    return useQuery(
        'customerAccounts',
        async () => {
            const response = await fetch(baseUrl + '/accounts', {
                method: 'get',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                }),
            });
            const data: AccountDto[] = await response.json();
            return data;
        },
        { enabled: !!currentUser, refetchOnWindowFocus: false }
    );
};

const useAccountFullInfo = (currentUser: User | null, accountExternalId?: string) => {
    return useQuery(
        ['accountFullInfo'],
        async () => {
            const response = await fetch(baseUrl + '/account/' + accountExternalId, {
                method: 'get',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
            });
            const data: AccountFullInfoDto = await response.json();
            return data;
        },
        { enabled: !!accountExternalId && !!currentUser, refetchOnWindowFocus: false }
    );
};

const useRefreshAccountInfo = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (accountExternalId: string) => {
            await fetch(baseUrl + `/account/${accountExternalId}/refresh`, {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
            });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['accountFullInfo'] });
            queryClient.invalidateQueries({ queryKey: ['statsAccount'] });
        }
    });
};

const useGetAccountStats = (currentUser: User | null, request: GetStatsRequest, accountExternalId?: string) => {
    return useQuery(
        ['statsAccount', request.from.toDateString(), request.to.toDateString()],
        async () => {
            const response = await fetch(baseUrl + '/stats/account/' + accountExternalId, {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify(request)
            });
            const data: StatsDto = await response.json();
            return data;
        },
        { enabled: !!accountExternalId && !!currentUser, refetchOnWindowFocus: false }
    );
}

const useCreateCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (category: CategoryDto) => {
            await fetch(baseUrl + '/category/create', {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify(category)
            });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['accountFullInfo'] });
        }
    });
};

const useDeleteCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (id: number) => {
            await fetch(baseUrl + '/category/delete/' + id, {
                method: 'delete',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
            });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['accountFullInfo'] });
            queryClient.invalidateQueries({ queryKey: ['statsAccount'] });
        }
    });
};

const useEditTransactionCategories = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (request: EditTransactionCategories) => {
            await fetch(baseUrl + '/categorize/transaction', {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify(request)
            });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['accountFullInfo'] });
            queryClient.invalidateQueries({ queryKey: ['statsAccount'] });
        }
    });
};

const useCategorizeAccountTransactions = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (request: CategorizeAccountTransactionsRequest) => {
            await fetch(baseUrl + '/categorize/account', {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify(request)
            });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['accountFullInfo'] });
            queryClient.invalidateQueries({ queryKey: ['statsAccount'] });
        }
    });
};

export {
    useAccountFullInfo, useCategorizeAccountTransactions, useCountryInstitutions, useCreateCategory, useCreateCustomer, useCreateRequisition, useCustomerAccounts, useDeleteCategory,
    useEditTransactionCategories, useGetAccountStats, useRefreshAccountInfo
};

