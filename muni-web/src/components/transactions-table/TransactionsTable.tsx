import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@mui/material";
import { TransactionDto } from "../../api/dtos"

type Props = {
    transactions: TransactionDto[];
};

const TransactionsTable = ({ transactions }: Props) => {
    return (
        <TableContainer sx={{ height: 'inherit', width: 'inherit' }}>
            <Table aria-label="simple table" stickyHeader>
                <TableHead>
                    <TableRow>
                        <TableCell>Reference from institution</TableCell>
                        <TableCell align="justify">Amount&nbsp;($)</TableCell>
                        <TableCell align="justify">Booking date</TableCell>
                        <TableCell align="justify">Information</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {transactions.map((transaction) => (
                        <TableRow
                            key={transaction.externalId}
                            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                        >
                            <TableCell component="th" scope="row">
                                {transaction.refFromInstitution}
                            </TableCell>
                            <TableCell align="justify">{transaction.amount}</TableCell>
                            <TableCell align="justify">{transaction.bookingDate.toString()}</TableCell>
                            <TableCell align="justify">{transaction.remittanceInfo}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}

export default TransactionsTable;